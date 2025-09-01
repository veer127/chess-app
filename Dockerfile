FROM openjdk:17-slim

# Install required dependencies
RUN apt-get update && apt-get install -y wget unzip

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and source
COPY . .

# Give permission to gradlew
RUN chmod +x ./gradlew

# Build the APK
RUN ./gradlew clean assembleDebug
