import React from 'react';
import './PredictionOutput.css';

const PredictionOutput = ({ predictionResult }) => {
 
  // Determine if the container should be visible based on predictionResult
  const isVisible = !!predictionResult;
  
  // Determine if high-score styling should apply
  const isHighScore = isVisible && parseFloat(predictionResult.probability) > 0.50;

  return (
    <div
      className={`message-container prediction-output-wrapper ${isVisible ? 'visible' : ''} ${
        isHighScore ? 'high-score' : ''
      }`}
    >
      <div className="prediction-output">
        <p>{predictionResult ? predictionResult.message : ''}</p>
      </div>
    </div>
  );
};

export default PredictionOutput;