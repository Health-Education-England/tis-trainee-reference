FROM openjdk:11-slim
EXPOSE 8205
COPY build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]