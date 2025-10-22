package OrangeCloud.UserRepo.service;

import OrangeCloud.UserRepo.dto.auth.AuthResponse;
import OrangeCloud.UserRepo.dto.auth.LoginRequest;
import OrangeCloud.UserRepo.dto.auth.SignupRequest;
import OrangeCloud.UserRepo.dto.userinfo.UserInfoResponse;
import OrangeCloud.UserRepo.entity.User;
import OrangeCloud.UserRepo.repository.UserRepository;
import OrangeCloud.UserRepo.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    // 로그아웃된 토큰들을 저장할 블랙리스트 (실제로는 Redis 등을 사용하는 것이 좋습니다)
    private final Set<String> tokenBlacklist = new HashSet<>();

    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse signup(SignupRequest signupRequest) {
        // 이메일 중복 검사
        if (userRepository.existsByEmailAndIsActiveTrue(signupRequest.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 사용자 생성
        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .passwordHash(passwordEncoder.encode(signupRequest.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        // JWT 토큰 생성
        String accessToken = tokenProvider.generateToken(savedUser.getUserId());
        String refreshToken = tokenProvider.generateRefreshToken(savedUser.getUserId());

        return new AuthResponse(accessToken, refreshToken, savedUser.getUserId(), savedUser.getName(), savedUser.getEmail());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        // 사용자 찾기
        User user = userRepository.findByEmailAndIsActiveTrue(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("등록되지 않은 이메일입니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String accessToken = tokenProvider.generateToken(user.getUserId());
        String refreshToken = tokenProvider.generateRefreshToken(user.getUserId());

        return new AuthResponse(accessToken, refreshToken, user.getUserId(), user.getName(), user.getEmail());
    }

    public void logout(String token) {
        // 토큰 유효성 검사
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 토큰을 블랙리스트에 추가
        tokenBlacklist.add(token);
    }

    public AuthResponse refreshToken(String refreshToken) {
        // Refresh 토큰 유효성 검사
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 refresh token입니다.");
        }

        // Refresh 토큰에서 사용자 ID 추출
        UUID userId = tokenProvider.getUserIdFromRefreshToken(refreshToken);

        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 새로운 토큰 생성
        String newAccessToken = tokenProvider.generateToken(user.getUserId());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getUserId());

        return new AuthResponse(newAccessToken, newRefreshToken, user.getUserId(), user.getName(), user.getEmail());
    }

    public UserInfoResponse getCurrentUserInfo(String email) {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return new UserInfoResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(), // username으로 email 사용
                user.getName(),
                LocalDateTime.now() // 현재 시간으로 대체
        );
    }

    // 토큰이 블랙리스트에 있는지 확인하는 메서드
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
}