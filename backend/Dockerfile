# Use a JDK image to compile your Java code
FROM eclipse-temurin:11-jdk-focal AS build

WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean install -DskipTests

# Use a JRE image since we only need to run the JAR
FROM eclipse-temurin:11-jre-focal

WORKDIR /app

# Copy the shaded JAR from the build stage
COPY --from=build /app/target/predict.jar ./predict.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "predict.jar"]

