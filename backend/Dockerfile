# ---- Build Stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Önce sadece pom.xml kopyala (dependency cache için)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Sonra kaynak kodu kopyala ve build et
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]