# Use an official Maven image to build the application
FROM maven:3.9.9-eclipse-temurin-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the parent pom.xml
COPY pom.xml .

# Copy all service directories
COPY ./ .

# Accept the service name as a build argument
ARG SERVICE_NAME

# Build the specific service using the passed argument
RUN mvn -pl ${SERVICE_NAME} clean package -DskipTests

# Use a lightweight Java image for the final container
FROM eclipse-temurin:17-jre

# Set the working directory in the container
WORKDIR /app

# Accept the service name as a build argument
ARG SERVICE_NAME

# Copy the JAR file of the specific service from the build stage
COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
