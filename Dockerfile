FROM gradle:8.7-jdk21-alpine AS builder

WORKDIR /app
COPY . .

RUN gradle build -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]