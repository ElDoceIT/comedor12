FROM eclipse-temurin:21.0.4_7-jdk
ARG JAR_FILE=target/comedor-0.4.3.jar
COPY ${JAR_FILE} comedor.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "comedor.jar"]