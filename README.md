# MindfulNet-AI

<p style='text-align: center; font-size: 17.5px; color: white;'>
    The average American spends <strong style='color: white;'>~2.5 hours</strong> per day on social media.
</p>

<p style='text-align: center; font-size:17.5px; color: white;'>
    Over the course of 1 year, this equates to <span style='color: red; font-weight: bold;'>more than 1 month</span> wasted on social media.
</p>
        
<p style='text-align: center; font-size: 17.5px; color: white; font-weight: bold;'>
    This is <span style='color: white; font-weight: bold;'>5.7</span> <span style='color: red; font-weight: bold;'>or more years</span> spent on social media platforms GONE by the age of 73!
</p>

## Usage and Interaction

MindfulNet-AI is a full stack application that predicts social media addiction based on user-provided information, including social media habits and socioeconomic background. It uses a random forest machine learning algorithm built from scratch in Java.

You can try out the app live here: [MindfulNet-AI](https://claudio-perinuzzi.github.io/MindfulNet-AI/)

<center>
<img src="assets/smup_img.png" width="400">
</center>

## Introduction

In today’s age, social media consumption has been rapidly increasing as companies vie for user attention. This competition for user attention has caused social media companies to employ new features designed to retain users on their platform for longer. One recent example was the emergence of Tik-Tok, a social media platform that became popular for its main feature that plays short video clips with each swipe of the finger. This simple feature takes advantage of a user’s small attention span to try and keep them on the platform for longer. This feature was so effective that other companies such as Youtube and Instagram have implemented their own versions of this feature. As a result, social media consumption has skyrocketed and the absence of regulations in this area causes concern for a user’s well-being. 

This system is designed to combat these predatory features indirectly by providing the user with a prediction on how likely the user is to spend an excessive amount of time on social media. The system will also provide the user with customized recommendations and local resources so that the user can make informed decisions about their social media usage. Our system is specifically designed for individual users who wish to develop healthier habits. 

## Example Usage of Program

### User Interface:

The user begins by entering their information through a clean, intuitive user interface. Inputs can include social media habits, socioeconomic data, and other relevant fields required for prediction.

### Prediction Results:

After submitting input, the model processes the data and provides a prediction. In this case, the prediction will indicate the likelihood of social media addiction based on the user's data. The result is presented clearly, along with any additional insights or suggestions generated by the model.

## System and Random Forest Interaction Modeling Diagrams

### Model-View-Controller Pattern

The system follows the Model-View-Controller (MVC) pattern, which separates user interface and core model logic. The high level architectural design of the system is as follows:

<img src="assets/uml_diagram1.png" alt="MVC Architecture" width="350"/>


### Training the Model

The Random Forest model was implemented in Java from scratch. First, a `DataContainer` object is instantiated with the training dataset. This dataset is bootstrapped at the Decision Tree level, meaning random samples (with replacement) are drawn to create multiple datasets for each decision tree. This introduces more variety for each Decision Tree.

For each tree, a Decision Tree is recursively built using feature selection. At each node, an algorithm calculates the maximum information gain using Gini impurity to determine the best feature to split the data on. Once the best feature is determined, the node splits the data on this feature and the process continues for each node until a pure leaf is detected.

These decision trees are then collected into an array within the `RandomForest` object. This object can then be used to predict, given new user input, whether the user is at risk of social media addiction.

The dataset is split into a train (80%) and test set (20%). The model achieves a 98% accuracy on the test set.

The following sequence diagram outlines the classes involved and the training process on the dataset:

<img src="assets/uml_sequence_functional1.png" alt="Random Forest Training" width="600"/>


### Using the Model to Make a Prediction

Since the model was trained on a `DataContainer` object containing the dataset, a new `DataContainer` object is instantiated when the user provides input. This new object is subsequently fed into the `RandomForest` for prediction. The user's input is then processed through the array of Decision Trees within the Random Forest, where individual predictions are aggregated. A majority vote is conducted to determine whether the user is at risk of addiction.

<img src="assets/uml_sequence_functional2.png" alt="Prediction On User Data" width="450"/>


### Generating a Report Based on the Prediction:

Upon generating a prediction, the system creates a report for user review. If the user is identified as being at risk of social media addiction, the system will notify them of this risk and provide additional resources for support. Conversely, if the user is not at risk, the system will commend their healthy habits and offer resources to help them maintain their positive behavior.

The resources provided are tailored to the user's input, taking into account factors such as location, interests, and socioeconomic background when suggesting relevant support materials.
 
<img src="assets/uml_sequence_functional3.png" alt="Report" width="450"/>


