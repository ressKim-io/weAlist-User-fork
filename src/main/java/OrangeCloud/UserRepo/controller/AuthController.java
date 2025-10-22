package OrangeCloud.UserRepo.controller;

import OrangeCloud.UserRepo.dto.*;
import OrangeCloud.UserRepo.dto.auth.AuthResponse;
import OrangeCloud.UserRepo.dto.auth.LoginRequest;
import OrangeCloud.UserRepo.dto.auth.SignupRequest;
import OrangeCloud.UserRepo.dto.userinfo.UserInfoResponse;
import OrangeCloud.UserRepo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import OrangeCloud.UserRepo.dto.MessageApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자 계정을 생성합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    public ResponseEntity<MessageApiResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            AuthResponse authResponse = authService.signup(signupRequest);
            return ResponseEntity.ok(new MessageApiResponse(true, "회원가입이 완료되었습니다.", authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 인증을 수행하고 JWT 토큰을 발급합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 실패")
    public ResponseEntity<MessageApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(new MessageApiResponse(true, "로그인이 완료되었습니다.", authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 세션을 종료하고 토큰을 무효화합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    public ResponseEntity<MessageApiResponse> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            authService.logout(token);
            return ResponseEntity.ok(new MessageApiResponse(true, "로그아웃이 완료되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 토큰 갱신
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 Access Token을 갱신합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 갱신 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 토큰")
    public ResponseEntity<MessageApiResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshRequest) {
        try {
            AuthResponse authResponse = authService.refreshToken(refreshRequest.getRefreshToken());
            return ResponseEntity.ok(new MessageApiResponse(true, "토큰이 갱신되었습니다.", authResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 내 정보 조회
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 인증된 사용자의 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    public ResponseEntity<MessageApiResponse> getCurrentUser(Authentication authentication) {
        try {
            UserInfoResponse userInfo = authService.getCurrentUserInfo(authentication.getName());
            return ResponseEntity.ok(new MessageApiResponse(true, "사용자 정보 조회 성공", userInfo));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageApiResponse(false, e.getMessage()));
        }
    }

    // 헬퍼 메서드: Request에서 토큰 추출
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("Authorization 헤더에서 토큰을 찾을 수 없습니다.");
    }
}