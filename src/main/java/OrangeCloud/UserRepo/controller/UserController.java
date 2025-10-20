package OrangeCloud.UserRepo.controller;

import OrangeCloud.UserRepo.dto.MessageApiResponse;
import OrangeCloud.UserRepo.dto.user.UpdateUserRequest;
import OrangeCloud.UserRepo.dto.user.UserResponse;
import OrangeCloud.UserRepo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Users", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 사용자 목록 조회 (팀원 검색용)
    @GetMapping
    @Operation(summary = "사용자 목록 조회", description = "모든 사용자 목록을 조회합니다. 검색 및 페이징을 지원합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    public ResponseEntity<MessageApiResponse> getAllUsers(
            @Parameter(description = "검색 키워드 (이름 또는 이메일)")
            @RequestParam(required = false) String search,
            @Parameter(description = "페이징 정보")
            Pageable pageable) {
        try {
            if (search != null && !search.trim().isEmpty()) {
                Page<UserResponse> users = userService.searchUsers(search, pageable);
                return ResponseEntity.ok(new MessageApiResponse(true, "사용자 검색 완료", users));
            } else {
                Page<UserResponse> users = userService.getAllUsers(pageable);
                return ResponseEntity.ok(new MessageApiResponse(true, "사용자 목록 조회 완료", users));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 특정 사용자 조회
    @GetMapping("/{id}")
    @Operation(summary = "특정 사용자 조회", description = "ID로 특정 사용자 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    public ResponseEntity<MessageApiResponse> getUserById(@PathVariable UUID id) {
        try {
            UserResponse user = userService.getUserById(id);
            return ResponseEntity.ok(new MessageApiResponse(true, "사용자 조회 완료", user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound()
                    .build();
        }
    }

    // 사용자 정보 수정
    @PatchMapping("/{id}")
    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다. 자신의 정보만 수정 가능합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    public ResponseEntity<MessageApiResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest updateRequest,
            Authentication authentication) {
        try {
            // 자신의 정보만 수정할 수 있도록 권한 체크
            UserResponse updatedUser = userService.updateUser(id, updateRequest, authentication);
            return ResponseEntity.ok(new MessageApiResponse(true, "사용자 정보가 수정되었습니다.", updatedUser));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(403)
                        .body(new MessageApiResponse(false, e.getMessage()));
            } else if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageApiResponse(false, e.getMessage()));
            }
        }
    }

    // 사용자 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다. 자신의 계정만 삭제 가능합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    public ResponseEntity<MessageApiResponse> deleteUser(
            @PathVariable UUID id,
            Authentication authentication) {
        try {
            userService.deleteUser(id, authentication);
            return ResponseEntity.ok(new MessageApiResponse(true, "사용자가 삭제되었습니다."));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("권한")) {
                return ResponseEntity.status(403)
                        .body(new MessageApiResponse(false, e.getMessage()));
            } else if (e.getMessage().contains("찾을 수 없습니다")) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageApiResponse(false, e.getMessage()));
            }
        }
    }
}