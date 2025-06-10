package com.perinuzzi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perinuzzi.Main.PredictHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.nio.charset.StandardCharsets;
import java.net.InetSocketAddress;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map; 

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * MAIN
 * Loads in a random forest ML model to use for inference
 * Starts an HTTP backend server listening on port 8080
 * 
 * Starting the backend:
 *      mvn clean install
 *      java -jar target/predict.jar  
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class Main {

    private static RandomForest forest; // Model is accessible statically
    private static final ObjectMapper objectMapper = new ObjectMapper(); // For JSON, thread safe
    public static void main(String[] args) throws IOException {
        
        System.out.println("Starting backend server...");

        // Attempt to load the model
        forest = loadSerializedModelFromClasspath(); 
        if (forest == null) { // Otherwise train a new model
            System.out.println("Model not loaded. Training a new one...");
            forest = trainNewModel(100, 4); 
        }

        // HTTP Server Setup
        int port = 8080; // Default port

        // Check if a port argument is provided and parse it
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]); 
                System.out.println("Starting server on port: " + port);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number provided: " + args[0] + ". Using default port: " + port);
            }
        } 
        else System.out.println("Starting server on default port: " + port);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("Server listening on port " + port);

        // Create a context for predictions (POST requests to /predict)
        server.createContext("/predict", new PredictHandler());

        // Start the server & use default executor
        server.setExecutor(null); 
        server.start();
    }

    // HTTP Handler for /predict endpoint 
    static class PredictHandler implements HttpHandler {
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle CORS Preflight Requests (OPTIONS method)
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                addCorsHeaders(exchange);
                exchange.sendResponseHeaders(204, -1); // No content
                return;
            }

            // Ensure it's a POST request for prediction
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                String response = "Only POST requests are allowed for /predict endpoint";
                exchange.sendResponseHeaders(405, response.length()); // Method Not Allowed
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            // Set CORS Headers for actual responses
            addCorsHeaders(exchange);

            String responseJson = "";
            int statusCode = 200; // OK

            try {
                if (forest == null) {
                    statusCode = 500;
                    responseJson = "{\"error\": \"Prediction model is not loaded on the server. Cannot make predictions.\"}";
                } 
                else {
                    // Read the request body (JSON input from frontend)
                    InputStream requestBody = exchange.getRequestBody();
                    String requestJson = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Received request body (JSON): " + requestJson);
    
                    // Parse the JSON into a Map to build the comma-sep string for the DataContainer class
                    java.util.Map<String, Object> jsonMap = objectMapper.readValue(requestJson, java.util.Map.class);
    
                    // If the user spends over 8 hours on social media per day on average, then the user is very likely to be addicted
                    PredictionResult result = new PredictionResult();
                    int time = Integer.parseInt(String.valueOf(jsonMap.get("timeSpent")));
                    if (time >= 8) {                        
                        double probability = 1.0;
                        String message = "Probability of addiction: " + String.format("%.0f", probability * 100) + "%. ";
                        result = new PredictionResult(message, probability);
                    }
                    else if (time == 0) {
                        double probability = 0.0;
                        String message = "Probability of addiction: " + String.format("%.0f", probability * 100) + "%. ";
                        result = new PredictionResult(message, probability);                    
                    }
                    else {
                        // Build the comma-separated string for the DataContainer object and ensure keys match JSON keys from frontend
                        String commaSeparatedInput = String.join(",",
                            String.valueOf(jsonMap.get("age")), 
                            String.valueOf(jsonMap.get("gender")), 
                            String.valueOf(jsonMap.get("timeSpent")), 
                            String.valueOf(jsonMap.get("platform")), 
                            String.valueOf(jsonMap.get("interests")),
                            String.valueOf(jsonMap.get("location")),
                            String.valueOf(jsonMap.get("demographics")),
                            String.valueOf(jsonMap.get("profession")),
                            String.valueOf(jsonMap.get("income")),
                            String.valueOf(jsonMap.get("indebt")),
                            String.valueOf(jsonMap.get("isHomeOwner")),
                            String.valueOf(jsonMap.get("OwnsCar"))
                        );
                        System.out.println("Converted to comma-separated string: " + commaSeparatedInput);
        
                        DataContainer userInput = new DataContainer(commaSeparatedInput);
                        System.out.println("Parsed DataContainer from string: " + userInput); 
        
                        // Make prediction
                        result = predictAndReturn(forest, userInput);  
                    }

                    responseJson = objectMapper.writeValueAsString(result);
                }
    
            } catch (Exception e) {
                statusCode = 400;
                responseJson = "{\"error\": \"Failed to process request: " + e.getMessage() + "\"}";
                System.err.println("Error in PredictHandler: " + e.getMessage());
                e.printStackTrace();
            } finally {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(statusCode, responseJson.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseJson.getBytes());
                os.close();
            }
        }

        // Helper method to add CORS headers
        private void addCorsHeaders(HttpExchange exchange) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // Allow all origins for development
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        }
    }

    // Determine the outcome by running the user's input against the forest model and counting the predictions  
    public static PredictionResult predictAndReturn(RandomForest forest, DataContainer userInput) {
        
        int[] predictions = forest.aggregate(userInput);
        int yes = 0, no = 0;

        for (int i = 0; i < predictions.length; i++) {
            if (predictions[i] == 1) yes++;
            else no++;
        }

        double probability = (double) yes / (yes + no);

        String message = "Probability of addiction: " + String.format("%.0f", probability * 100) + "%. ";
        if (yes > no) {
            message += "You may be at risk of social media addiction!";
        } else {
            message += "Your social media usage is within a healthy range!";
        }
        
        return new PredictionResult(message, probability);
    }

    // Train the model with n decision trees
    public static RandomForest trainNewModel(int numTrees, int numFeatures) {
        RandomForest forest = new RandomForest();
        forest.train(numTrees, numFeatures);
        return forest;
    }

    public static RandomForest loadSerializedModelFromClasspath() {
        String modelResourcePath = "/model/randomForestModel.ser";
        System.out.println("Attempting to load model from classpath: " + modelResourcePath);

        try (InputStream is = Main.class.getResourceAsStream(modelResourcePath)) {
            if (is == null) {
                System.err.println("ERROR: Model resource NOT found at " + modelResourcePath +
                                   ". Ensure 'src/main/resources/model/randomForestModel.ser' is in your project.");
                return null;
            }
            try (ObjectInputStream ois = new ObjectInputStream(is)) { 
                RandomForest forest = (RandomForest) ois.readObject();
                System.out.println("Random Forest model deserialized successfully from classpath.");
                return forest;
            }
        } catch (IOException e) {
            System.err.println("IOException during model deserialization from classpath: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException during model deserialization from classpath: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ClassCastException e) {
            System.err.println("ClassCastException: The object loaded is not a RandomForest instance.");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during classpath model loading: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}