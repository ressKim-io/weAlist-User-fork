# weAlist-User
UserRepo는 Spring Boot를 이용한 사용자 관리 프로젝트입니다. 사용자, 그룹, 팀을 관리하는 API를 제공합니다.

🚀 Cloud Native Ready: Kubernetes 배포를 위한 베이스 애플리케이션

🎯 프로젝트 개요
이 프로젝트는 향후 Kubernetes 환경으로 마이그레이션하기 위한 베이스 애플리케이션입니다. 현재는 Docker Compose로 실행하며, K8s 배포에 필요한 기능들이 이미 구현되어 있습니다.

## 주요 기능
✅ User Management: 사용자 회원가입, 로그인, 프로필 관리  
✅ Group Management: 그룹 생성 및 멤버 관리   
✅ Team Management: 팀 단위 워크스페이스 관리   
✅ JWT Authentication: Access/Refresh Token 기반 인증   
✅ RESTful API: 표준 REST API 설계  

## 🛠️ 기술 스택
### Backend
- Java 17 - 최신 LTS 버전
- Spring Boot 3.5.6 - 최신 Spring Boot
- Spring Security - 보안 및 인증
- Spring Data JPA - 데이터 접근 계층
- JWT (JSON Web Token) - 토큰 기반 인증

### Infrastructure
- PostgreSQL - 관계형 데이터베이스
- Docker & Docker Compose - 컨테이너 오케스트레이션
- Swagger/OpenAPI 3 - API 문서화

### Cloud Native Features
✅ JWT Token Management (30분 Access, 7일 Refresh)  
✅ Graceful Shutdown   
✅ 12-Factor App Compliance  
✅ Stateless Design  

## 🚀 빠른 시작
### 사전 요구사항
- Docker & Docker Compose
- Java 17
- Git

### 1. 저장소 클론 및 설정
```bash
# 저장소 클론
git clone https://github.com/OrangesCloud/weAlist-User.git
cd weAlist-User

# 환경변수 파일 생성
cp .env.example .env

# .env 파일 수정 (중요!)
# POSTGRES_PASSWORD와 JWT_SECRET을 변경하세요
nano .env
```

### 2. 애플리케이션 실행
#### Docker Compose 사용 (권장)
```bash
# 전체 환경 시작 (PostgreSQL + UserRepo)
docker-compose up -d

# 로그 확인
docker-compose logs -f userrepo

# 상태 확인
docker-compose ps
```

#### 로컬 개발 환경
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 또는 JAR 파일 실행
java -jar build/libs/UserRepo-0.0.1-SNAPSHOT.jar
```

### 3. 접속 확인
```bash
# API 문서
open http://localhost:8080/swagger-ui.html
```

## 📡 API 엔드포인트
### 문서
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs
- Health Check: http://localhost:8080/health

### 주요 API
| 엔드포인트                 | 설명         | 인증     |
|--------------------------|--------------|----------|
| `POST /api/auth/signup`  | 회원가입     | No       |
| `POST /api/auth/login`   | 로그인       | No       |
| `POST /api/auth/refresh` | 토큰 갱신    | Required |
| `GET /api/auth/userinfo` | 사용자 정보  | Required |
| `GET /api/users`         | 사용자 목록  | Required |
| `GET /api/groups`        | 그룹 목록    | Required |
| `GET /api/teams`         | 팀 목록      | Required |

자세한 API 명세는 Swagger UI 참고

## 🧪 테스트
```bash
# 전체 테스트 실행
./gradlew test

# 테스트 리포트 확인
open build/reports/tests/test/index.html

# 특정 테스트만 실행
./gradlew test --tests "*.AuthControllerTest"
```

## 📁 프로젝트 구조
```bash
src/
    ├── main/
    │   ├── java/OrangeCloud/UserRepo/
    │   │   ├── config/           # Spring Security, Swagger 등 설정
    │   │   ├── controller/       # API 엔드포인트 컨트롤러
    │   │   │   ├── AuthController.java
    │   │   │   ├── UserController.java
    │   │   │   └── GroupController.java
    │   │   ├── dto/              # 데이터 전송 객체
    │   │   │   ├── auth/         # 인증 관련 DTO
    │   │   │   ├── user/         # 사용자 관련 DTO
    │   │   │   └── userinfo/     # 사용자 정보 DTO
    │   │   ├── entity/           # JPA 엔티티
    │   │   ├── repository/       # JPA 리포지토리
    │   │   ├── service/          # 비즈니스 로직 서비스
    │   │   └── util/             # 유틸리티 (JWT 등)
    │   │       └── JwtTokenProvider.java
    │   └── resources/
    │       ├── application.yml   # 애플리케이션 설정
    │       └── application-*.yml # 환경별 설정
    ├── test/                     # 테스트 코드
    ├── docker-compose.yml
    ├── Dockerfile
    └── build.gradle             # 프로젝트 빌드 및 의존성
```

## 🔧 개발 가이드
### 로컬 개발 환경 설정
```bash
# PostgreSQL 컨테이너만 실행
docker run -d \
  --name postgres-dev \
  -e POSTGRES_DB=userrepo \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:15

# 애플리케이션 개발 모드 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### JWT 토큰 설정
현재 토큰 만료 시간:

- Access Token: 30분 (1800000ms)
- Refresh Token: 7일 (604800000ms)

```yaml
# application.yml
app:
  jwt-secret: "your-secret-key-here"
  jwt-expiration-ms: 1800000  # 30분
```

### 컨테이너 재빌드
```bash
# 서비스 중지
docker-compose down

# 재빌드 후 시작
docker-compose up -d --build

# 로그 확인
docker-compose logs -f userrepo
```

## 🚧 향후 계획 (Phase 2)
### Kubernetes 마이그레이션
- Helm Chart 작성
- ConfigMap/Secret 분리
- HPA (Horizontal Pod Autoscaler) 설정
- PersistentVolume 구성

### CI/CD 파이프라인
- GitHub Actions 워크플로우
- 자동 빌드 & 배포
- 컨테이너 이미지 레지스트리 (ECR/GCR)
- 자동 테스트 실행

### 모니터링 & 로깅
- Prometheus + Grafana
- ELK Stack 또는 Loki
- 분산 추적 (Jaeger/Zipkin)
- 알림 설정 (Slack/Discord)

### 보안 강화
- Network Policy 설정
- RBAC 구성
- Secret 암호화 (Sealed Secrets)
- 컨테이너 보안 스캔 (Trivy)

## 🛠️ 트러블슈팅
### 포트 충돌
```bash
# 포트 사용 확인
lsof -i :8080

# 포트 변경 (application.yml)
server:
  port: 8081
```

### 데이터베이스 연결 실패
```bash
# PostgreSQL 상태 확인
docker-compose ps

# PostgreSQL 로그 확인
docker-compose logs postgres

# 연결 테스트
docker-compose exec postgres psql -U postgres -d userrepo
```

### JWT 토큰 오류
```bash
# 토큰 검증 실패시 시크릿 키 확인
app.jwt-secret=your-secret-key-must-be-at-least-64-bytes

# 토큰 만료시 재로그인 필요
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Authorization: Bearer YOUR_REFRESH_TOKEN"
```

## 🤝 기여
### 개발 환경
- IDE: IntelliJ IDEA 또는 VS Code
- Java: OpenJDK 17
- Build Tool: Gradle 7.x

### 참고 자료
- Spring Boot Documentation
- Spring Security Reference
- JWT.io
- 12-Factor App

## 📄 라이선스
이 프로젝트는 학습 목적으로 개발되었습니다.