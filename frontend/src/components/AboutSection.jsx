import React from 'react';
import './AboutSection.css';

const AboutSection = () => {
  return (
    <div className="about-section-content">
      <h1 className="Title">
        Mindful<span className="ai-gradient-left">NET</span>{' '}
        <span className="ai-gradient-right">AI</span>
      </h1>
      <p>
        This application utilizes a Random Forest Machine Learning model to predict
        the likelihood of social media addiction based on various user attributes.
        It's designed to provide insights and help users reflect on their social media habits
        and provide healthy alternatives to social media.
      </p>
      <p>
        The model was trained on a dataset of user social media profiles, learning
        patterns and correlations to make its predictions. This tool
        provides an estimation and should not be used as a substitute for professional
        medical advice.
      </p>
      <p>
        For any concerns about social media usage, please consult with a qualified professional.
      </p>
    </div>
  );
};

export default AboutSection;