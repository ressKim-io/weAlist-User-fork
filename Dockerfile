#
# Build stage
#
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

COPY gradlew* ./
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon

#
# Runtime stage
#
FROM eclipse-temurin:17-jre-jammy AS runner

# 타임존 설정 & 필요 패키지 설치
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 비root 사용자 생성
RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app
USER spring:spring

COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar

# 올바른 헬스체크 설정
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]