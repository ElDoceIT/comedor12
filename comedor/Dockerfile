FROM openjdk:21-ea-33-jdk-bullseye
ARG JAR_FILE=target/comedor-0.0.2.jar
COPY ${JAR_FILE} comedor.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "comedor.jar"]