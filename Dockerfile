# ---------------------- Build Stage ----------------------
FROM gradle:8.7-jdk21 AS build

WORKDIR /app

# Copy Gradle configuration
COPY build.gradle settings.gradle ./
COPY gradlew ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

RUN chmod +x gradlew

# Build the application (bootJar)
RUN ./gradlew clean bootJar --no-daemon


# ---------------------- Runtime Stage ----------------------
FROM eclipse-temurin:21-jdk AS runtime


WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=build /app/build/libs/*.jar app.jar

# Render provides PORT env variable automatically
ENV PORT=8080

EXPOSE 8080

# Java entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]
