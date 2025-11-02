# --- STAGE 1: BUILD ---
# Use a Maven image with a stable JDK version (e.g., 21 LTS)
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to leverage Docker layer caching for dependencies
COPY pom.xml .

# Download dependencies (only if pom.xml has changed)
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the application, skipping tests
RUN mvn clean package -DskipTests

# --- STAGE 2: RUNTIME ---
# Use a smaller, lightweight JRE image for the final application
FROM eclipse-temurin:21-jre-alpine

# Set the time zone
ENV TZ=UTC
ENV CATALINA_OPTS="-Duser.timezone=UTC"

# Set application environment variable for the JAR file path
ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar

# Expose the default application port (Docker Compose will map this)
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
