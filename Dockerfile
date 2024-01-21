FROM openjdk:17-jdk-alpine

# 인자 설정 - Jar_File
ARG JAR_FILE=build/libs/*.jar

# jar 파일 복제
COPY ${JAR_FILE} config.jar

# 실행 명령어
ENTRYPOINT ["java","-jar","config.jar"]