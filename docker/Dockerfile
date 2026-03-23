# ----------- BUILD STAGE -----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app


COPY . .

RUN mvn -f backend/equis/pom.xml clean package -DskipTests


# ----------- RUN STAGE -----------
FROM eclipse-temurin:21-jdk

WORKDIR /app


COPY --from=builder /app/backend/equis/target/*.jar app.jar


EXPOSE 8443


ENTRYPOINT ["java", "-jar", "app.jar"]
