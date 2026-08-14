FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --chown=spring:spring target/*.jar app.jar
EXPOSE 8080
USER spring:spring
ENTRYPOINT ["java","-jar","app.jar"]