# 보안 강화 및 JWT 인증 가이드

이 문서는 `weAlist-User` 프로젝트에서 발견된 보안 취약점을 해결하고, 모든 프로젝트에서 참고할 수 있는 안전한 JWT 인증 구현 방식을 안내하기 위해 작성되었습니다.

---

## Part 1: `weAlist-User` 프로젝트 보안 문제 수정 계획

### 1. 수정이 필요한 파일 목록

- `src/main/java/OrangeCloud/UserRepo/util/JwtTokenProvider.java`
- `src/main/java/OrangeCloud/UserRepo/config/SecurityConfig.java`
- `src/main/resources/application.properties`
- `.gitignore`
- (신규 생성) `src/main/java/OrangeCloud/UserRepo/config/JwtAuthenticationFilter.java`

### 2. 문제점 및 해결 방안

#### 문제 1: JWT 비밀키 하드코딩 및 관리 부실

- **파일**: `JwtTokenProvider.java`
- **문제**: JWT 비밀키가 코드 내에 하드코딩되어 있어, 소스 코드 접근 권한이 있는 사람에게 그대로 노출됩니다.
- **해결**: 하드코딩된 키를 제거하고, 외부 환경 변수(`app.jwt-secret`)가 없을 경우 애플리케이션이 실행되지 않도록 수정합니다.

**수정 예시 (`JwtTokenProvider.java` 생성자)**

```java
// 수정 전 (Before)
public JwtTokenProvider(@Value("${app.jwt-secret:}") String jwtSecret,
                        @Value("${app.jwt-expiration-ms:604800000}") int jwtExpirationInMs) {

    // 기본 시크릿이 비어있거나 너무 짧은 경우 안전한 기본값 사용
    if (jwtSecret == null || jwtSecret.trim().isEmpty() || jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
        jwtSecret = "OrangeCloudSecretKeyForJWTTokenGenerationMustBeAtLeast64BytesLongForHS512Algorithm2024";
    }
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    this.jwtExpirationInMs = jwtExpirationInMs;
}

// 수정 후 (After)
public JwtTokenProvider(@Value("${app.jwt-secret}") String jwtSecret,
                        @Value("${app.jwt-expiration-ms}") int jwtExpirationInMs) {
    if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
        throw new IllegalArgumentException("JWT secret key cannot be null or empty.");
    }
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    if (keyBytes.length < 64) {
        throw new IllegalArgumentException("JWT secret key must be at least 64 bytes long for HS512 algorithm.");
    }
    this.key = Keys.hmacShaKeyFor(keyBytes);
    this.jwtExpirationInMs = jwtExpirationInMs;
}
```

#### 문제 2: 민감 정보(DB 비밀번호) 노출

- **파일**: `application.properties`
- **문제**: DB 비밀번호의 기본값이 `'1234'`로 설정되어 있어, Git에 커밋될 경우 유출 위험이 있습니다.
- **해결**: 기본값을 제거하여 반드시 환경 변수를 통해서만 비밀번호를 설정하도록 강제합니다.

**수정 예시 (`application.properties`)**

```properties
# 수정 전 (Before)
spring.datasource.password=${DB_PASSWORD:1234}

# 수정 후 (After)
spring.datasource.password=${DB_PASSWORD}
```

#### 문제 3: JWT 인증 필터 부재 및 과도한 접근 허용

- **파일**: `SecurityConfig.java`
- **문제**: JWT 토큰을 검증하는 필터가 없고, 대부분의 API가 인증 없이 접근 가능(`permitAll`)합니다.
- **해결**:
    1.  요청 헤더의 토큰을 검증하는 `JwtAuthenticationFilter`를 새로 구현합니다.
    2.  `SecurityConfig`에 이 필터를 등록하고, API 경로별 접근 권한을 명확히 설정합니다.

**수정 예시 (`SecurityConfig.java`)**

```java
// 1. JwtAuthenticationFilter를 주입받도록 필드 추가
private final JwtAuthenticationFilter jwtAuthenticationFilter;

// 2. filterChain 메소드 수정
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                    // 인증 없이 접근 허용할 경로
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api/auth/**").permitAll()
                    // 특정 역할(Role)이 필요한 경로
                    // .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    // 나머지는 모두 인증 필요
                    .anyRequest().authenticated()
            )
            // 직접 구현한 JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
}
```
*(참고: `JwtAuthenticationFilter`의 전체 코드는 별도 구현이 필요합니다.)*

#### 문제 4: 민감 정보 설정 파일 관리 방안

- **파일**: `.gitignore`, `application.properties`
- **문제**: 로컬 개발 환경에서 사용할 민감 정보를 안전하게 관리하는 표준 방식이 없습니다.
- **해결**:
    1.  로컬 전용 설정 파일(`application-local.properties`)을 사용합니다.
    2.  `.gitignore`에 `*-local.properties` 패턴을 추가하여 Git 저장소에 포함되지 않도록 합니다.
    3.  Spring Profile을 `local`로 활성화하여 해당 설정 파일을 사용합니다.

**`.gitignore` 파일에 추가할 내용**

```
# Local-specific properties
*-local.properties
```

**로컬 개발용 `application-local.properties` 파일 예시**

```properties
# DB
DB_HOST=localhost
DB_PORT=5432
DB_NAME=wealist_db_local
DB_USER=wealist_user
DB_PASSWORD=your_strong_local_password

# JWT
app.jwt-secret=your_super_long_and_random_jwt_secret_for_local_env_only
app.jwt-expiration-ms=3600000 # 1시간
```

---

## Part 2: 다른 팀을 위한 JWT 인증 구현 가이드

### 1. 핵심 원칙

- **비밀은 코드와 분리한다**: JWT 비밀키, API 키 등 모든 민감 정보는 소스 코드 및 버전 관리 시스템과 완벽히 분리합니다.
- **Access Token은 짧게, Refresh Token은 길게**: Access Token(15분~1시간)은 탈취 시 피해를 최소화하고, Refresh Token(7일~30일)으로 새 토큰을 발급받습니다.
- **HTTPS는 필수**: JWT는 내용이 암호화되지 않으므로(서명만 됨), 통신 과정 탈취를 막기 위해 항상 HTTPS를 사용해야 합니다.
- **토큰에는 최소한의 정보만 담는다**: 토큰에는 사용자 식별자(ID), 역할(Role) 등 꼭 필요한 정보만 포함하고, 개인정보는 담지 않습니다.

### 2. 비밀키(Secret Key) 관리

- **절대 하지 말 것 (DON'T)**
    - 소스 코드에 비밀키 하드코딩
    - `.properties`, `.yml` 등 설정 파일에 비밀키를 그대로 작성하여 Git에 커밋

- **반드시 할 것 (DO)**
    - **환경 변수 사용**: 모든 환경(로컬, 개발, 운영)에서 환경 변수를 통해 비밀키를 주입받습니다.
    - **외부 Secrets Manager 사용**: 운영 환경에서는 AWS Secrets Manager, Google Secret Manager, HashiCorp Vault 등 전문 보안 서비스를 사용하는 것을 적극 권장합니다.
    - **강력한 키 생성**: `openssl rand -base64 64` 등의 명령어로 각 환경마다 길고 무작위적인 키를 생성하여 사용합니다.

### 3. 토큰 만료 및 폐기 정책

- **Access Token**: 만료 시간을 1시간 이하로 짧게 설정합니다.
- **Refresh Token**:
    - 만료 시간을 7일 이상으로 길게 설정할 수 있습니다.
    - **(중요)** DB(RDBMS 또는 Redis)에 Refresh Token을 저장하고, 사용자가 로그아웃하거나 비밀번호를 변경할 때 해당 토큰을 DB에서 삭제하여 강제로 폐기(Revoke)할 수 있는 로직을 반드시 구현해야 합니다.

### 4. 안전한 Spring Security 설정 예시

아래는 `JwtAuthenticationFilter`가 적용된 안전한 `SecurityConfig`의 골격 예시입니다.

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 주입
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint unauthorizedHandler; // 인증 실패 시 처리

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 기본 설정
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults()) // CORS 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 예외 처리
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(unauthorizedHandler))

                // 경로별 접근 제어
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())

                // 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // BCryptPasswordEncoder, CorsConfigurationSource 등 기타 Bean 설정...
}
```
