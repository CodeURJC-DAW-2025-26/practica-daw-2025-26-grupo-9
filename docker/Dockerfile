# -------- BUILD --------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY . .

WORKDIR /app/backend/equis

RUN mvn clean package -DskipTests

# -------- RUNTIME --------
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/backend/equis/target/equis-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]
