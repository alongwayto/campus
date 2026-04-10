# Build stage
FROM maven:3.9-eclipse-temurin-11 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

RUN addgroup -S campus && adduser -S campus -G campus

COPY --from=builder /build/target/*.jar app.jar

RUN mkdir -p /app/backups /app/models && \
    chown -R campus:campus /app

USER campus

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "-Xms256m", "-Xmx512m", "app.jar"]
