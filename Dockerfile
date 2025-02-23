FROM openjdk:17-jdk-slim

# 2. 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=dev

WORKDIR /app

ARG JAR_FILE=build/libs/poppin-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]
