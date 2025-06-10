# Step 1: Use Maven to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .
RUN mvn package

# Step 2: Use a lightweight Java runtime to run the app
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/predict.jar ./predict.jar

# Copy the CSV data file into the image
COPY src/main/resources/data/user_social_media_profiles_train.csv ./src/main/resources/data/user_social_media_profiles_train.csv

EXPOSE 8080

CMD ["java", "-jar", "predict.jar"]
