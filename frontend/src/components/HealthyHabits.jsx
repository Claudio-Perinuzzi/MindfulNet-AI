import React from 'react';
import healthyHabitsD from '../data/healthyHabitsData'; 
import './HealthyHabits.css';

const HealthyHabits = () => {
  return (
    <div className="healthy-habits-container message-container visible">
      <h2>Recommendations for Healthier Social Media Habits:</h2>
      <ul>
        {healthyHabitsD.map((habit) => (
          <li key={habit.id}>
            <strong>{habit.title}:</strong> {habit.description}
          </li>
        ))}
      </ul>
      <p>
        For more in-depth support, please see the external resources below.
      </p>
    </div>
  );
};

export default HealthyHabits;