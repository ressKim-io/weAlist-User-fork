// UserService.java에 추가할 메서드들
package OrangeCloud.UserRepo.service;

import OrangeCloud.UserRepo.dto.user.UpdateUserRequest;
import OrangeCloud.UserRepo.dto.user.UserResponse;
import OrangeCloud.UserRepo.entity.User;
import OrangeCloud.UserRepo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 모든 사용자 조회 (페이징)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToUserResponse);
    }

    // 사용자 검색 (이름 또는 이메일로)
    public Page<UserResponse> searchUsers(String search, Pageable pageable) {
        return userRepository.searchUsers(search, pageable)
                .map(this::convertToUserResponse);
    }

    // 특정 사용자 조회
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return convertToUserResponse(user);
    }

    // 사용자 정보 수정
    public UserResponse updateUser(UUID id, UpdateUserRequest updateRequest, Authentication authentication) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 자신의 정보만 수정할 수 있도록 권한 체크
        if (!isOwnerOrAdmin(user, authentication)) {
            throw new RuntimeException("자신의 정보만 수정할 수 있습니다.");
        }

        // 정보 업데이트
        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }

        if (updateRequest.getEmail() != null) {
            // 이메일 중복 체크
            if (!user.getEmail().equals(updateRequest.getEmail()) &&
                    userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPassword() != null) {
            // 현재 비밀번호 확인
            if (updateRequest.getCurrentPassword() == null ||
                    !passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPasswordHash())) {
                throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
            }
            user.setPasswordHash(passwordEncoder.encode(updateRequest.getPassword()));
        }

        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    // 사용자 삭제
    public void deleteUser(UUID id, Authentication authentication) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 자신의 계정만 삭제할 수 있도록 권한 체크
        if (!isOwnerOrAdmin(user, authentication)) {
            throw new RuntimeException("자신의 계정만 삭제할 수 있습니다.");
        }

        userRepository.deleteById(id);
    }

    // 권한 체크 헬퍼 메서드
    private boolean isOwnerOrAdmin(User user, Authentication authentication) {
        String currentUserEmail = authentication.getName();
        return user.getEmail().equals(currentUserEmail);
        // 관리자 권한이 있다면: || hasRole("ADMIN", authentication)
    }

    // Entity를 Response DTO로 변환
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}