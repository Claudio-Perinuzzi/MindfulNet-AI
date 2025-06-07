package com.perinuzzi;

import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Arrays;
import java.io.File;


/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * MAIN
 * Collects a command line argument used to either load in or train a new random forest model
 * which is then used to print back to the service whether the user is at risk of social
 * media addiction and what the corresponding confidence score is.
 * 
 * Legacy Deployment:
 *      javac --release 17 src/*.java
 *      jar cfm dist/predict.jar META-INF/MANIFEST.MF -C bin .
 * 
 * Maven Deployment (note first 3 CL args are used for determining what the user may like):
 *      javac -d bin src/main/java/com/perinuzzi/*.java
 *      mvn clean install                                                         
 *      java -jar target/predict.jar True Jefferson hiking 60 male 5 Instagram Travel United Urban Student 12500 False True False
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class Main {
    public static void main(String[] args)  {
        
        // Get the arg index 0 flag on whether the user wants to use the serialized model 
        boolean toSerialize = (args[0].equals("True")); 

        // Get the rest of the users input arguments
        String input = String.join(",", Arrays.copyOfRange(args, 3, args.length)); 

        // Create a DataContainer object of the user's input 
        DataContainer userInput = new DataContainer(input); 

        // Load the serialized model into the forest and predict
        RandomForest forest = null;
        if (toSerialize) {
            System.out.println("Loading pre-serialized model...");
            forest = loadSerializedModelFromClasspath(); 
            if (forest == null) {
                System.err.println("Failed to load serialized model. Exiting.");
                return; // Exit if model not loaded
            }
        }
        // Or, train a new model with 100 trees and predict
        else {
            System.out.println("Training a new model...");
            forest = trainNewModel(100, 4); 
        }

        // Ensure forest is not null before predicting
        if (forest != null) {
            predict(forest, userInput);
        } else {
            System.err.println("Model is null. Cannot make predictions.");
        }

    }

    // Determine the outcome by running the user's input against the forest model and counting the predictions  
    public static void predict(RandomForest forest, DataContainer userInput) {
        
        // Get predictions from the forest and initialize label counts for voting
        int[] predictions = forest.aggregate(userInput); 
        int yes = 0, no = 0; 
        
        // Count the yes and no labels
        for (int i = 0; i < predictions.length; i++) {
            if (predictions[i] == 1) yes++;
            else no++;
        }
        
        // Calculate probability of addiction
        double probability = (double) yes / (yes + no);
        
        // Print whether the user may be addicted to social media and offer suggestions
        if (yes > no) {
            System.out.println("You may be at risk of social media addiction!");
            System.out.println("Probability of addiction: " + String.format("%.0f", probability * 100) + "%");
        } else {
            System.out.println("Your social media usage is within a healthy range!");
            System.out.println("Probability of addiction: " + String.format("%.0f", probability * 100) + "%");
        }
    }


    // Train the model with n decision trees
    public static RandomForest trainNewModel(int numTrees, int numFeatures) {
        RandomForest forest = new RandomForest();
        forest.train(numTrees, numFeatures);   
        return forest; 
    }


    public static RandomForest loadSerializedModelFromClasspath() {
        // Path the model exists within the JAR (classpath)
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
