package com.perinuzzi;

import java.io.Serializable;

/*****************************************************************
 * PREDICTION RESULT CLASS
 *      Conversion to JSON for sending back to the front end
 *****************************************************************/
 
public class PredictionResult implements Serializable { 
    private String message;
    private double probability;

    // Default constructor for Jackson deserialization
    public PredictionResult() {}

    public PredictionResult(String message, double probability) {
        this.message = message;
        this.probability = probability;
    }

    // public getters for Jackson to serialize to JSON
    public String getMessage() { return message; }
    public double getProbability() { return probability; }

}
