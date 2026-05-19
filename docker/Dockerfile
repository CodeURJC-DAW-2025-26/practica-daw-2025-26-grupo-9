# ----------- FRONTEND BUILD (React SPA) -----------
FROM node:22-alpine AS frontend-builder

WORKDIR /app

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ .
RUN npm run build

# ----------- BACKEND BUILD (Spring Boot) -----------
FROM maven:3.9.9-eclipse-temurin-21 AS backend-builder

WORKDIR /app

COPY backend ./backend

COPY --from=frontend-builder /app/build/client ./backend/equis/src/main/resources/static/new

RUN mvn -f backend/equis/pom.xml clean package -DskipTests

# ----------- RUN -----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=backend-builder /app/backend/equis/target/*.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]
