FROM openjdk:17-jdk-slim

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=dev
ENV AWS_ACCESS_KEY=${AWS_ACCESS_KEY}
ENV AWS_SECRET_KEY=${AWS_SECRET_KEY}
ENV AWS_REGION=${AWS_REGION}

WORKDIR /app

ARG JAR_FILE=build/libs/poppin-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
