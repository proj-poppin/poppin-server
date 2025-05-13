FROM openjdk:17-jdk-slim

# 환경변수 설정
ENV SPRING_PROFILES_ACTIVE=prd

# 작업 디렉토리 생성
WORKDIR /app


COPY build/libs/*.jar poppin-server-0.0.1-SNAPSHOT.jar

# 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prd", "-jar", "/app/poppin-server-0.0.1-SNAPSHOT.jar"]