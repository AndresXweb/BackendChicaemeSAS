# ========================
# Stage 1: Build
# ========================
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ========================
# Stage 2: Runtime
# ========================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xms128m -Xmx400m -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
