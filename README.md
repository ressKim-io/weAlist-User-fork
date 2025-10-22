# UserRepo

UserRepo는 Spring Boot를 이용한 사용자 관리 프로젝트입니다. 사용자, 그룹, 팀을 관리하는 API를 제공합니다.

## 버전 정보

- **Java:** 17
- **Spring Boot:** 3.5.6

## 주요 의존성

- Spring Data JPA
- Spring Web
- Spring Security
- Spring Boot Validation
- Lombok
- JSON Web Token (JWT)
- SpringDoc OpenAPI (Swagger)
- PostgreSQL Driver

## 프로젝트 구조

```
.
├── src
│   ├── main
│   │   ├── java
│   │   │   └── OrangeCloud
│   │   │       └── UserRepo
│   │   │           ├── config         # Spring Security, Swagger 등 설정
│   │   │           ├── controller     # API 엔드포인트 컨트롤러
│   │   │           ├── dto            # 데이터 전송 객체
│   │   │           ├── entity         # JPA 엔티티
│   │   │           ├── repository     # JPA 리포지토리
│   │   │           ├── service        # 비즈니스 로직 서비스
│   │   │           └── util           # 유틸리티 클래스 (JWT 등)
│   │   └── resources
│   │       └── application.properties # 애플리케이션 설정
│   └── test
│       └── java
└── build.gradle                       # 프로젝트 빌드 및 의존성 관리
```

## 설치 및 실행

### 1. 프로젝트 빌드

프로젝트 루트 디렉토리에서 다음 명령어를 실행하여 프로젝트를 빌드합니다.

```bash
./gradlew build
```

### 2. 애플리케이션 실행

빌드가 완료되면 다음 명령어를 사용하여 애플리케이션을 실행할 수 있습니다.

```bash
java -jar build/libs/UserRepo-0.0.1-SNAPSHOT.jar
```

또는 Gradle을 사용하여 직접 실행할 수도 있습니다.

```bash
./gradlew bootRun
```

애플리케이션이 실행되면 기본적으로 8080 포트에서 실행됩니다.

## API 엔드포인트

API 문서는 애플리케이션 실행 후 `http://localhost:8080/swagger-ui.html` 에서 확인할 수 있습니다.

### 인증 (Authentication) - `/api/auth`

- `POST /signup`: 회원가입
- `POST /login`: 로그인
- `POST /logout`: 로그아웃
- `POST /refresh`: JWT 토큰 갱신
- `GET /me`: 내 정보 조회

### 그룹 (Group) - `/api/groups`

- `POST /`: 그룹 생성
- `GET /company/{companyName}/all`: 회사별 모든 그룹 조회
- `POST /force-new`: 강제 새 그룹 생성
- `GET /company/{companyName}`: 회사명으로 그룹 조회
- `GET /check-company/{companyName}`: 회사명 중복 체크
- `GET /`: 활성화된 모든 그룹 조회
- `GET /{groupId}`: 특정 그룹 조회
- `GET /search`: 회사명으로 그룹 검색
- `GET /search/name`: 그룹명으로 그룹 검색
- `PUT /{groupId}`: 그룹 정보 수정
- `DELETE /{groupId}`: 그룹 삭제 (Soft delete)
- `PUT /{groupId}/reactivate`: 그룹 재활성화
- `GET /count`: 활성화된 그룹 수 조회
- `GET /inactive`: 비활성화된 그룹 조회

### 팀 (Team) - `/api/teams`

- `POST /`: 팀 생성
- `GET /`: 활성화된 모든 팀 조회
- `GET /{teamId}`: 특정 팀 조회
- `GET /group/{groupId}`: 그룹별 팀 조회
- `GET /leader/{leaderId}`: 팀장별 팀 조회
- `GET /{teamId}/members`: 팀 멤버 목록 조회
- `GET /{teamId}/members/count`: 팀 멤버 수 조회
- `GET /available-users/{groupId}`: 팀에 추가 가능한 사용자 목록 조회
- `GET /{teamId}/leader/check`: 팀장 권한 확인
- `GET /user/{userId}`: 사용자가 속한 팀 조회
- `GET /user/{userId}/led-teams`: 사용자가 팀장인 팀 조회
- `PUT /{teamId}`: 팀 정보 수정
- `PUT /{teamId}/leader`: 팀장 변경
- `DELETE /{teamId}`: 팀 삭제
- `DELETE /{teamId}/disband`: 팀 해산
- `POST /{teamId}/members`: 팀에 멤버 추가
- `DELETE /{teamId}/members/{userId}`: 팀에서 멤버 제거

### 사용자 (User) - `/api/users`

- `DELETE /{userId}`: 사용자 삭제 (Soft delete)
- `GET /`: 활성화된 모든 사용자 조회
- `GET /{userId}`: 특정 사용자 조회
- `GET /email/{email}`: 이메일로 사용자 조회
- `GET /search`: 이름으로 사용자 검색
- `PUT /{userId}`: 사용자 정보 수정
- `PATCH /{userId}/password`: 비밀번호 변경
- `PUT /{userId}/reactivate`: 사용자 재활성화
- `POST /login`: 로그인
- `GET /check-email`: 이메일 중복 체크
- `GET /count`: 활성화된 사용자 수 조회

### 사용자 정보 (UserInfo) - `/api/userinfo`

- `DELETE /{userId}`: 사용자 정보 삭제 (Soft delete)
- `GET /`: 활성화된 모든 사용자 정보 조회
- `GET /group/{groupId}`: 그룹별 사용자 정보 조회
- `GET /role/{role}`: 역할별 사용자 정보 조회
- `PUT /{userId}/reactivate`: 사용자 정보 재활성화
- `DELETE /group/{groupId}`: 그룹의 모든 사용자 정보 삭제
- `GET /count`: 활성화된 사용자 정보 수 조회
- `POST /`: 사용자 정보 생성
- `PUT /{userId}`: 사용자 정보 수정
