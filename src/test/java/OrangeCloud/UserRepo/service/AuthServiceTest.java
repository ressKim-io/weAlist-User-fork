package OrangeCloud.UserRepo.service;

import OrangeCloud.UserRepo.dto.auth.AuthResponse;
import OrangeCloud.UserRepo.dto.auth.LoginRequest;
import OrangeCloud.UserRepo.dto.auth.SignupRequest;
import OrangeCloud.UserRepo.entity.User;
import OrangeCloud.UserRepo.repository.UserRepository;
import OrangeCloud.UserRepo.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest("Test User", "test@example.com", "password");
        user = User.builder()
                .userId(UUID.randomUUID())
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .passwordHash("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        when(userRepository.existsByEmailAndIsActiveTrue(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenProvider.generateToken(any(UUID.class))).thenReturn("accessToken");
        when(tokenProvider.generateRefreshToken(any(UUID.class))).thenReturn("refreshToken");

        // when
        AuthResponse authResponse = authService.signup(signupRequest);

        // then
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getAccessToken()).isEqualTo("accessToken");
        assertThat(authResponse.getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시 예외 발생")
    void signup_emailAlreadyExists() {
        // given
        when(userRepository.existsByEmailAndIsActiveTrue(anyString())).thenReturn(true);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            authService.signup(signupRequest);
        }, "이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        when(userRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(tokenProvider.generateToken(any(UUID.class))).thenReturn("accessToken");
        when(tokenProvider.generateRefreshToken(any(UUID.class))).thenReturn("refreshToken");

        // when
        AuthResponse authResponse = authService.login(loginRequest);

        // then
        assertThat(authResponse).isNotNull();
        assertThat(authResponse.getAccessToken()).isEqualTo("accessToken");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외 발생")
    void login_userNotFound() {
        // given
        LoginRequest loginRequest = new LoginRequest("wrong@example.com", "password");
        when(userRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        }, "등록되지 않은 이메일입니다.");
    }

    @Test
    @DisplayName("비밀번호 불일치로 로그인 시 예외 발생")
    void login_passwordMismatch() {
        // given
        LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongpassword");
        when(userRepository.findByEmailAndIsActiveTrue(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when & then
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        }, "비밀번호가 일치하지 않습니다.");
    }
}
