# 안전한 JWT 인증 구현을 위한 공통 가이드

이 문서는 모든 프로젝트에서 공통적으로 참고할 수 있는 안전한 JWT(JSON Web Token) 인증 방식 구현 가이드를 제공합니다.

---

### 1. 핵심 원칙

- **비밀은 코드와 분리한다**: JWT 비밀키, API 키 등 모든 민감 정보는 소스 코드 및 버전 관리 시스템과 완벽히 분리합니다.
- **Access Token은 짧게, Refresh Token은 길게**: Access Token(15분~1시간)은 탈취 시 피해를 최소화하고, Refresh Token(7일~30일)으로 새 토큰을 발급받습니다.
- **HTTPS는 필수**: JWT는 내용이 암호화되지 않으므로(서명만 됨), 통신 과정 탈취를 막기 위해 항상 HTTPS를 사용해야 합니다.
- **토큰에는 최소한의 정보만 담는다**: 토큰에는 사용자 식별자(ID), 역할(Role) 등 꼭 필요한 정보만 포함하고, 개인정보는 담지 않습니다.

---

### 2. JWT 비밀키 보관 및 사용 방법

JWT 비밀키는 절대 소스 코드나 Git 저장소에 노출되어서는 안 됩니다. 아래 방법 중 하나를 사용하여 안전하게 관리해야 합니다.

#### 방법 1: 환경 변수 사용 (가장 표준적인 방식)

운영, 개발 등 모든 환경에서 가장 권장되는 방식입니다. 애플리케이션이 실행되는 서버 환경에 변수를 직접 설정합니다.

**1. 비밀키 생성 (터미널)**

```bash
# 64바이트(512비트) 이상의 강력한 무작위 키를 생성합니다.
openssl rand -base64 64
```

**2. 환경 변수 설정 (Linux/macOS)**

```bash
# 위에서 생성한 키를 서버의 환경 변수로 등록합니다.
export APP_JWT_SECRET="your-super-long-and-random-jwt-secret-key-here"
```

**3. Spring Boot에서 사용 (`application.properties`)**

Spring Boot는 `${...}` 구문을 통해 자동으로 환경 변수를 읽어옵니다.

```properties
app.jwt-secret=${APP_JWT_SECRET}
```

#### 방법 2: 로컬 전용 설정 파일 사용 (로컬 개발 환경용)

매번 환경 변수를 설정하기 번거로운 로컬 개발 환경에서 사용할 수 있는 편리한 방법입니다.

**1. 로컬 전용 설정 파일 생성 (`application-local.properties`)**

`src/main/resources/` 경로에 로컬 환경에서만 사용할 파일을 생성합니다.

```properties
# application-local.properties

# JWT
app.jwt-secret=your-long-and-random-secret-for-local-development-only

# DB
spring.datasource.username=local_db_user
spring.datasource.password=local_db_password
```

**2. `.gitignore`에 추가 (매우 중요)**

이 파일이 Git 저장소에 올라가지 않도록 `.gitignore`에 반드시 추가합니다.

```
# Local-specific properties
*-local.properties
*-local.yml
```

**3. Spring Profile 활성화**

`application.properties`에 아래 설정을 추가하여 `local` 프로파일을 활성화합니다. 이렇게 하면 `application-local.properties` 파일의 설정을 덮어쓰게 됩니다.

```properties
# application.properties
spring.profiles.active=local
```

#### 방법 3: 외부 Secrets Manager 사용 (클라우드/운영 환경)

가장 안전하고 중앙화된 관리 방식입니다. 애플리케이션 실행 시 외부 보안 저장소에서 민감 정보를 동적으로 가져옵니다.

- **서비스 예시**: AWS Secrets Manager, Google Secret Manager, Azure Key Vault, HashiCorp Vault
- **동작 방식**: 애플리케이션이 시작될 때 해당 클라우드 서비스의 SDK를 통해 비밀키를 메모리로 직접 로드합니다. 이 방식은 코드나 서버 환경에 비밀키를 직접 노출하지 않습니다.

---

### 3. 토큰 만료 및 폐기 정책

- **Access Token**: 만료 시간을 1시간 이하로 짧게 설정합니다.
- **Refresh Token**:
    - 만료 시간을 7일 이상으로 길게 설정할 수 있습니다.
    - **(중요)** DB(RDBMS 또는 Redis)에 Refresh Token을 저장하고, 사용자가 로그아웃하거나 비밀번호를 변경할 때 해당 토큰을 DB에서 삭제하여 강제로 폐기(Revoke)할 수 있는 로직을 반드시 구현해야 합니다.

---

### 4. 안전한 Spring Security 설정 예시

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
