# weAlist-User 프로젝트 보안 진단 보고서

이 문서는 `weAlist-User` 프로젝트의 현재 JWT 구현 방식과 발견된 보안 취약점을 기술하고, Git 버전 관리에서 제외해야 할 대상을 명시하기 위해 작성되었습니다.

---

### 1. 현재 JWT 생성 및 암호화 방식

- **JWT 생성 주체**: `src/main/java/OrangeCloud/UserRepo/util/JwtTokenProvider.java` 클래스가 Access Token과 Refresh Token 생성을 담당합니다.
- **암호화(서명) 알고리즘**: 토큰 서명 시 `HS512` 해시 알고리즘을 사용합니다. 이는 강력한 대칭키 암호화 방식 중 하나입니다.
- **토큰 내용**: 토큰에는 사용자를 식별하기 위한 ID와 토큰 만료 시간이 포함됩니다.

### 2. 발견된 보안 취약점

1.  **JWT 비밀키 소스 코드 노출**
    - **위치**: `JwtTokenProvider.java`
    - **문제**: 외부 설정(`app.jwt-secret`)이 없을 경우, 소스 코드 내에 하드코딩된 특정 문자열을 JWT 비밀키로 사용합니다. 이는 소스 코드 접근 권한이 있는 모든 사람에게 비밀키가 노출되는 심각한 취약점입니다.

2.  **데이터베이스 기본 비밀번호 노출**
    - **위치**: `src/main/resources/application.properties`
    - **문제**: 데이터베이스 연결 비밀번호의 기본값이 `'1234'`와 같이 매우 취약한 값으로 설정되어 있습니다. 이 설정 파일이 버전 관리에 포함될 경우, 내부 시스템의 접속 정보가 유출될 위험이 있습니다.

3.  **미완성된 인증/인가 로직**
    - **위치**: `src/main/java/OrangeCloud/UserRepo/config/SecurityConfig.java`
    - **문제**: JWT 토큰의 유효성을 검증하는 핵심 인증 필터(`JwtAuthenticationFilter`)가 구현 및 적용되어 있지 않습니다. 또한, 대부분의 API 엔드포인트가 인증 없이도 접근 가능하도록 `permitAll()`로 설정되어 있어, JWT 인증이 실질적으로 아무런 보호 역할을 하지 못하고 있습니다.

### 3. Git 버전 관리 제외 대상 (`.gitignore`)

프로젝트의 보안을 위해, 민감 정보를 포함할 수 있는 아래와 같은 파일들은 반드시 `.gitignore` 파일에 추가하여 버전 관리에서 제외해야 합니다.

```
# Intellij
.idea/
*.iml

# Gradle
.gradle/
build/

# Log files
*.log

# OS generated files
.DS_Store

# Local-specific properties (매우 중요)
# 로컬 환경 전용 설정 파일 (DB 접속 정보, JWT 비밀키 등 보관)
*-local.properties
*-local.yml

# Environment variables file (선택적)
.env
```
