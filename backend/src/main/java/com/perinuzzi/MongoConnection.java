package com.perinuzzi;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoConnection {

    private static MongoClient mongoClient;
    private static String databaseName;
    private static final Logger LOGGER = Logger.getLogger(MongoConnection.class.getName());

    // Private constructor to prevent instantiation
    private MongoConnection() { }

    public static void initialize() {
        
        if (mongoClient == null) {
            String connectionUriString; 

            // Directly attempt to load from config.properties
            LOGGER.info("Attempting to load MongoDB URI from config.properties...");
            Properties props = new Properties();
            try (InputStream input = MongoConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
                if (input == null) {
                    LOGGER.severe("Sorry, unable to find config.properties in the classpath.");
                    throw new RuntimeException("config.properties not found. Please create it at src/main/resources.");
                }
                props.load(input);
                connectionUriString = props.getProperty("mongodb.connection.uri");
                if (connectionUriString == null || connectionUriString.isEmpty()) {
                    LOGGER.severe("mongodb.connection.uri not found or is empty in config.properties.");
                    throw new RuntimeException("MongoDB connection URI not configured in config.properties.");
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error loading config.properties: " + ex.getMessage(), ex);
                throw new RuntimeException("Failed to load database configuration from config.properties.", ex);
            }

            if (connectionUriString == null || connectionUriString.isEmpty()) {
                 // This check should technically not be hit
                 // But kept as a failsafe for unexpected scenarios
                 LOGGER.severe("MongoDB connection URI is null or empty after loading from config.properties.");
                 throw new RuntimeException("MongoDB connection URI not available.");
            }

            try {
                ConnectionString connectionString = new ConnectionString(connectionUriString);
                databaseName = connectionString.getDatabase();
                if (databaseName == null || databaseName.isEmpty()) {
                    databaseName = "MindfulNetDB"; // Default DB name if not in URI
                    LOGGER.warning("Database name not specified in connection URI in config.properties. Using default: " + databaseName);
                }

                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
                        .build();

                mongoClient = MongoClients.create(settings);
                LOGGER.info("MongoDB client initialized successfully using config.properties.");

                // Optional: Test connection
                mongoClient.getDatabase(databaseName).runCommand(new Document("ping", 1));
                LOGGER.info("Successfully connected to MongoDB database: " + databaseName);

            } catch (MongoException e) {
                LOGGER.log(Level.SEVERE, "Failed to connect to MongoDB: " + e.getMessage(), e);
                mongoClient = null;
                throw new RuntimeException("Failed to initialize MongoDB connection.", e);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An unexpected error occurred during MongoDB initialization: " + e.getMessage(), e);
                mongoClient = null;
                throw new RuntimeException("Unexpected error during MongoDB initialization.", e);
            }
        }
    }

    public static MongoClient getClient() {
        if (mongoClient == null) {
            throw new IllegalStateException("MongoDB client has not been initialized. Call initialize() first.");
        }
        return mongoClient;
    }

    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            throw new IllegalStateException("MongoDB client has not been initialized. Call initialize() first.");
        }
        if (databaseName == null || databaseName.isEmpty()) {
             throw new IllegalStateException("Database name not determined. Ensure connection URI specifies a database or set it explicitly.");
        }
        return mongoClient.getDatabase(databaseName);
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            LOGGER.info("MongoDB client connection closed.");
        }
    }
}