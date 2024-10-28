# Use a lightweight OpenJDK base image
FROM openjdk:17-jdk-slim

# Expose port 8070 (the same as your application)
EXPOSE 8070

# Set the application home directory
ENV APP_HOME /usr/src/app

# Create the app directory inside the container
WORKDIR $APP_HOME

# Copy the JAR file from the local machine to the container
COPY target/shopping-cart-0.0.1-SNAPSHOT.jar app.jar

# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "app.jar"]
