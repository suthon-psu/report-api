# Stage 1: Build the application
FROM gradle:7.4-jdk17 AS build

WORKDIR /app

# Copy the Gradle configuration files
COPY build.gradle settings.gradle ./

# Copy the Gradle wrapper
COPY gradlew ./
COPY gradle ./gradle

# Download dependencies and cache them
RUN ./gradlew dependencies

# Copy the project source
COPY src ./src

# Build the application
RUN ./gradlew build

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-bullseye

RUN apt install libfreetype6

WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the jar file 
ENTRYPOINT ["java", "-jar", "app.jar"]