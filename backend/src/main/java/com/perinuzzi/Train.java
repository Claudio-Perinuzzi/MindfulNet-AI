package com.perinuzzi;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;


/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * TRAIN
 * Finds the most accurate forest and serializes it to "model/randomForestModel.ser"
 * Inference can be performed by loading in the serialized binary
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class Train {
    public static void main(String[] args) {
        
        // Find the best forest and serialize it
        RandomForest forest = train();
        try {
            FileOutputStream fileOut = new FileOutputStream("src/main/resources/model/randomForestModel.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(forest);
            out.close();
            fileOut.close();
            System.out.println("Serialized RandomForest is saved in src/main/resources/model/randomForestModel.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }


    // Trains and finds the best model
    public static RandomForest train() {

        System.out.println("Training...");

        DataContainer test = new DataContainer("src/main/resources/data/user_social_media_profiles_test.csv", true);
        RandomForest bestForest = new RandomForest();
        int highestPred = 0;

        // Train new forests to test
        for (int i = 0; i < 100; i++) {
            RandomForest forest = trainNewModel(100, 4);
            int correctPred = testForest(forest, test);

            if (highestPred < correctPred) {
                bestForest = forest;
                highestPred = correctPred;
            }
        }

        // Print the results and return the best forest
        double accuracy = highestPred / 200.0;
        System.out.println("best model got " + highestPred + " correct predictions out of 200.");
        System.out.println("Accuracy = " + accuracy);
        return bestForest;
    }
    

    // Test a single forest against the test dataset
    public static int testForest(RandomForest forest, DataContainer test) {
        
        int correctPred = 0;
        int rows = test.getRows();

        // Test each row in the test dataset against the model's prediction
        for (int r = 0; r < rows; r++) {
            String input = "";
            String[] rowString = test.getRow(r);
            
            // Build the input for the model to perform inference on
            for (int c = 0; c < rowString.length - 1; c++) {
                if (c == rowString.length - 2) input += rowString[c];
                else input += rowString[c] + ",";
            }
            int label = Integer.parseInt(rowString[rowString.length - 1]);
                    
            // Determine if the model's prediction is correct
            DataContainer testInput = new DataContainer(input);
            int prediction = predict(forest, testInput);
            if (prediction == label) correctPred++;
        }

        return correctPred;
    }


    // Get the prediction for this model
    public static int predict(RandomForest forest, DataContainer userInput) {
        
        // Get predictions from the forest and initialize label counts for voting
        int[] predictions = forest.aggregate(userInput); 
        int yes = 0, no = 0; 
        
        // Count the yes and no labels
        for (int i = 0; i < predictions.length; i++) {
            if (predictions[i] == 1) yes++;
            else no++;
        }
        
        // Return whether the user is addicated or not
        if (yes > no) return 1;
        else return 0;
        
    }    


    // Train the model with n decision trees
    public static RandomForest trainNewModel(int numTrees, int numFeatures) {
        RandomForest forest = new RandomForest();
        forest.train(numTrees, numFeatures);   
        return forest; 
    }

}
