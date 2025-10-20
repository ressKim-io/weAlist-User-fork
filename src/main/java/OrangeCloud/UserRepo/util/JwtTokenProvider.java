package OrangeCloud.UserRepo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final int jwtExpirationInMs;
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일

    public JwtTokenProvider(@Value("${app.jwt-secret:}") String jwtSecret,
                            @Value("${app.jwt-expiration-ms:604800000}") int jwtExpirationInMs) {

        // 기본 시크릿이 비어있거나 너무 짧은 경우 안전한 기본값 사용
        if (jwtSecret == null || jwtSecret.trim().isEmpty() || jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
            jwtSecret = "OrangeCloudSecretKeyForJWTTokenGenerationMustBeAtLeast64BytesLongForHS512Algorithm2024";
        }

        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    // Access Token 생성 (UUID 버전)
    public String generateToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Access Token 생성 (Long 버전) - AuthService와의 호환성을 위해 추가
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Refresh Token 생성
    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // Refresh Token 유효성 검사
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Refresh Token에서 사용자 ID 추출 (Long 반환)
    public UUID getUserIdFromRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return UUID.fromString(claims.getSubject()); // String을 UUID로 변환
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.", e);
        }
    }

    // Access Token에서 사용자 ID 추출 (UUID 반환)
    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return UUID.fromString(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.", e);
        }
    }

    // Access Token에서 사용자 ID 추출 (Long 반환) - AuthService와의 호환성을 위해 추가
    public Long getUserIdFromTokenAsLong(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.", e);
        }
    }

    // Token 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Token 만료 여부 확인
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    // Token 만료 시간 가져오기
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.", e);
        }
    }
}