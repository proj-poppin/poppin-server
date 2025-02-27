FROM openjdk:17-jdk-slim

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=dev

WORKDIR /app

# 실행할 사용자 생성 (보안 강화)
RUN useradd -m appuser
USER appuser

# JAR 파일을 변수 없이 복사 (ARG는 COPY에서 사용할 수 없음)
COPY build/libs/poppin-server-0.0.1-SNAPSHOT.jar app.jar

# 컨테이너에서 실행될 포트
EXPOSE 8080

# Spring Boot 실행 명령
CMD ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
