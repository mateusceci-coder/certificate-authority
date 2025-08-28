# =====================================================================================
# Stage 1: Build the application using Maven
# This stage uses a full JDK image to compile the Java code and build the JAR file.
# =====================================================================================
FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package -DskipTests


# =====================================================================================
# Stage 2: Create the final, optimized runtime image
# This stage uses a minimal JRE image for a smaller footprint and better security.
# =====================================================================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
