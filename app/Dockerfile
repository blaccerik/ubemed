FROM openjdk:11-jdk
ARG JAR_File=build/libs/app-0.0.1-SNAPSHOT.jar
COPY ${JAR_File} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 8080

