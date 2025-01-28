# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from build/libs
COPY build/libs/*.jar app.jar

# Expose the port your application runs on
EXPOSE 8082

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
