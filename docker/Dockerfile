# -------- BUILD --------
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar todo el repo
COPY . .

# Ir al backend real
WORKDIR /app/backend/equis

# Compilar
RUN mvn clean package -DskipTests

# -------- RUNTIME --------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiar jar generado
COPY --from=builder /app/backend/equis/target/equis-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]
