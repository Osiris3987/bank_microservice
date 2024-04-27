FROM maven:3.9.6-amazoncorretto-17 AS builder
COPY . /app
RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml clean package -DskipTests

FROM amazoncorretto:17-alpine3.19
RUN apk add --no-cache dumb-init && \
    addgroup --system appgroup && adduser --system --disabled-password --no-create-home appuser --ingroup appgroup
USER appuser
EXPOSE 8080

COPY --from=builder /app/target/hackathon_becoder_backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["dumb-init", "java", "-jar", "app.jar"]
