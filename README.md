# weAlist User Service

Spring Boot 기반 사용자 인증 및 관리 서비스

## 주요 기능

- ✅ **JWT 인증**: Access/Refresh Token 기반
- ✅ **사용자 관리**: 회원가입, 로그인, 프로필 관리
- ✅ **그룹 관리**: 그룹 생성 및 관리
- ✅ **팀 관리**: 팀 생성 및 멤버 관리
- ✅ **RESTful API**: 표준 REST API 설계

## 기술 스택

- **Java 17** + **Spring Boot 3.5.6**
- **Spring Security** + **JWT**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Redis 7** (세션/캐시)
- **Docker & Docker Compose**

## 빠른 시작

### 전제 조건
- Docker & Docker Compose
- Java 17 (로컬 개발 시)

### 실행 방법

```bash
# 루트 디렉토리에서 전체 환경 시작
docker-compose up -d

# User Service 로그 확인
docker logs -f wealist-user-service

# 접속 확인
curl http://localhost:8081/health
```

## API 문서

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **상세 API 문서**: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- **Health Check**: http://localhost:8081/health

### 주요 엔드포인트

| 엔드포인트 | 설명 | 인증 |
|-----------|------|------|
| `POST /api/auth/signup` | 회원가입 | No |
| `POST /api/auth/login` | 로그인 | No |
| `POST /api/auth/refresh` | 토큰 갱신 | No |
| `GET /api/auth/me` | 내 정보 조회 | Required |
| `GET /api/users` | 사용자 목록 | Required |
| `GET /api/groups` | 그룹 목록 | Required |
| `GET /api/teams` | 팀 목록 | Required |

## 로컬 개발

### Gradle 빌드 및 실행
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test
```

### Docker 재빌드
```bash
# User Service 재빌드
docker-compose up -d --build wealist-user-service

# 로그 확인
docker logs -f wealist-user-service
```

## 프로젝트 구조

```
src/main/java/OrangeCloud/UserRepo/
├── config/              # Spring Security, JWT, Redis 설정
├── controller/          # REST API 컨트롤러
│   ├── AuthController.java
│   ├── UserController.java
│   ├── GroupController.java
│   └── TeamController.java
├── dto/                 # 데이터 전송 객체
├── entity/              # JPA 엔티티
├── repository/          # JPA 리포지토리
├── service/             # 비즈니스 로직
└── util/                # JwtTokenProvider 등
```

## 환경 변수

주요 환경 변수는 루트의 `.env` 파일에서 관리됩니다:

```env
USER_SERVICE_PORT=8081
USER_DB_HOST=user-db
USER_REDIS_HOST=user-redis
JWT_SECRET=your-secret-key
JWT_EXPIRATION_MS=86400000
JWT_ACCESS_MS=1800000
```

## 트러블슈팅

### 포트 충돌
```bash
# 포트 사용 확인
lsof -i :8081

# 포트 변경 (.env 파일)
USER_SERVICE_PORT=8082
```

### 데이터베이스 연결 실패
```bash
# PostgreSQL 상태 확인
docker logs wealist-user-db

# 연결 테스트
docker exec -it wealist-user-db psql -U wealist_user -d wealist_user_db
```

### Redis 연결 실패
```bash
# Redis 상태 확인
docker logs wealist-user-redis

# Redis CLI 접속
docker exec -it wealist-user-redis redis-cli
```

## 관련 문서

- **API 상세 문서**: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- **Infrastructure 가이드**: [infrastructure/README.md](infrastructure/README.md)

## 라이선스

이 프로젝트는 학습 목적으로 개발되었습니다
