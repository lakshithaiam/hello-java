# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# copy pom + src and build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# copy jar from build stage
COPY --from=build /app/target/hello-java-1.0.0.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
