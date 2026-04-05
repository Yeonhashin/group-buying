# MCR 기반 OpenJDK 이미지 사용
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

# Spring Boot JAR 복사
COPY build/libs/*.jar app.jar

# 실행
ENTRYPOINT ["java","-jar","/app.jar"]