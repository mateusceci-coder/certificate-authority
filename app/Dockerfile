# =====================================================================================
# Stage 1: Build the application using Maven
# This stage uses a full JDK image to compile the Java code and build the JAR file.
# =====================================================================================
FROM eclipse-temurin:17-jdk-jammy as builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml first to leverage Docker's build cache.
# This layer is only rebuilt when the pom.xml or wrapper files change.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download all the dependencies next. This creates another cacheable layer.
# This step will be skipped if the pom.xml hasn't changed.
RUN ./mvnw dependency:go-offline

# Copy the rest of the application's source code
COPY src ./src

# Compile and package the application into a JAR file, skipping the tests.
# The resulting JAR will be in the /app/target directory.
RUN ./mvnw package -DskipTests


# =====================================================================================
# Stage 2: Create the final, optimized runtime image
# This stage uses a minimal JRE image for a smaller footprint and better security.
# =====================================================================================
FROM eclipse-temurin:17-jre-jammy

# Set the working directory
WORKDIR /app

# Create a non-root user and group for security
# Running the application as a non-root user is a critical security best practice.
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring

# Copy the executable JAR file from the 'builder' stage
# The JAR is located in the target directory of the builder stage.
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8080

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
