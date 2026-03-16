# Backend Dockerfile
# 1) Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace/app
COPY pom.xml .  
COPY src ./src
RUN mvn -B -DskipTests package

# 2) Runtime stage
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
COPY --from=build /workspace/app/target/solutionsa-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
