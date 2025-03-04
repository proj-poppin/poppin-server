FROM openjdk:17-jdk-slim

# AWS CLI 설치에 필요한 패키지 추가
RUN apt-get update && apt-get install -y curl unzip awscli

# 환경 변수 설정
ENV SPRING_PROFILES_ACTIVE=dev

WORKDIR /app

# 실행할 사용자 생성 (보안 강화)
RUN useradd -m appuser
USER appuser

# S3에서 JAR 다운로드 및 실행
CMD sh -c 'while ! aws s3 cp s3://$AWS_S3_DEV_BUCKET_NAME/app.jar /app/app.jar; do echo "Retrying S3 download..."; sleep 5; done && java -Dspring.profiles.active=dev -jar /app/app.jar'
