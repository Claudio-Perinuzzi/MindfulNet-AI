import React, { useState } from 'react';
import './App.css'; 
import PredictionForm from './components/PredictionForm';
import ExternalResources from './components/ExternalResources';
import PredictionOutput from './components/PredictionOutput';
import HealthyHabits from './components/HealthyHabits'; 
import AboutSection from './components/AboutSection';

function App() {
  const [predictionResult, setPredictionResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showAbout, setShowAbout] = useState(false); // Controls which main content pane is visible

  const handlePredict = async (formData) => {
    setLoading(true);
    setError(null);
    setPredictionResult(null);

    const dataToSend = formData;

    try {
      const response = await fetch('/api/predict', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(dataToSend),
      });

      if (!response.ok) {
        let errorBody = {};
        try {
          errorBody = await response.json();
        } catch (jsonError) {
          console.warn("Could not parse error response as JSON:", jsonError);
          errorBody = { error: `Server responded with status ${response.status} but no valid JSON error message.` };
        }
        throw new Error(errorBody.error || `HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      setPredictionResult(result);

    } catch (err) {
      console.error("Error making prediction request:", err);
      setError(err.message || "An unknown error occurred during prediction.");
    } finally {
      setLoading(false);
    }
  };
  console.log("Prediction Probability:", predictionResult?.probability);

  return (
    <div className="App">
      <header className="App-header">

        <button className="about-button" onClick={() => setShowAbout(!showAbout)}>
          {showAbout ? 'Go Back' : 'About'}
        </button>

        <div className="dynamic-content-wrapper">

          <div className={`about-section fade ${showAbout ? 'show' : ''}`}>
            {showAbout && ( 
              <AboutSection />
            )}
          </div>

          <div className={`form-section fade ${!showAbout ? 'show' : ''}`}>
            {!showAbout && ( 
              <>
                <PredictionForm onSubmit={handlePredict} loading={loading} />

                {/* Error Message Container */}
                <div className={`message-container ${error ? 'visible' : ''}`}>
                  <p className="error-message">{error}</p>
                </div>

                {/* Prediction Output Component */}
                <PredictionOutput predictionResult={predictionResult} />

                {/* Healthy Habits Recommendations Component */}
                {predictionResult && parseFloat(predictionResult.probability) > 0.50 && (
                  <HealthyHabits />
                )}

                {/* External Resources Component */}
                {predictionResult && parseFloat(predictionResult.probability) > 0.50 && (
                  <ExternalResources />
                )}
              </>
            )}
          </div>
        </div>
      </header>
    </div>
  );
}

export default App;