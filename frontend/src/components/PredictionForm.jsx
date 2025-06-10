import React, { useState } from 'react';
import './PredictionForm.css'; 

function PredictionForm({ onSubmit, loading }) {
  
  // Init state for each input field
  const [formData, setFormData] = useState({
    age: 18,
    gender: "male",
    timeSpent: 6,
    platform: "Facebook",
    interests: "Sports",
    location: "United States",
    demographics: "Urban",
    profession: "Student",
    income: 200,
    indebt: false,
    isHomeOwner: false,
    ownsCar: false,
  });

  // Handle changes to input fields
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  // Handle form submission
  const handleSubmit = (e) => {
    e.preventDefault(); // Prevent default form submission behavior
    onSubmit(formData); // Pass the form data to the parent component (App.jsx)
  };

  return (
    <>
      <h1 className="Title">
        Mindful<span className="ai-gradient-left">NET</span>{' '}
        <span className="ai-gradient-right">AI</span>
      </h1>

      <div className="Info-header">
        Are you addicted to social media?
        <br />
        <br />
        Find out below!
      </div>
      
      <form onSubmit={handleSubmit} className="prediction-form">
      <h2>Enter User Data:</h2>

      {/* Age */}
      <div className="form-group">
        <label htmlFor="age">Age:</label>
        <input
          type="number"
          id="age"
          name="age"
          value={formData.age}
          onChange={handleChange}
          min="1"
          max="130"
          required
        />
      </div>

      {/* Gender */}
      <div className="form-group">
        <label htmlFor="gender">Gender:</label>
        <select
          id="gender"
          name="gender"
          value={formData.gender}
          onChange={handleChange}
          required
        >
          <option value="male">Male</option>
          <option value="female">Female</option>
        </select>
      </div>

      {/* Time Spent */}
      <div className="form-group">
        <label htmlFor="timeSpent">Time Spent (hours/day):</label>
        <input
          type="number"
          id="timeSpent"
          name="timeSpent"
          value={formData.timeSpent}
          onChange={handleChange}
          min="0"
          max="24"
          required
        />
      </div>

      {/* Platform */}
      <div className="form-group">
        <label htmlFor="platform">Platform:</label>
        <select
          id="platform"
          name="platform"
          value={formData.platform}
          onChange={handleChange}
          required
        >
          <option value="Facebook">Facebook</option>
          <option value="Instagram">Instagram</option>
          <option value="Youtube">Youtube</option>
          <option value="Twitter">Twitter/X</option>
          <option value="LinkedIn">LinkedIn</option>
          <option value="TikTok">TikTok</option>
        </select>
      </div>

      {/* Interests */}
      <div className="form-group">
        <label htmlFor="interests">Interests/Hobbies:</label>
        <input
          type="text"
          id="interests"
          name="interests"
          value={formData.interests}
          onChange={handleChange}
          required
        />
      </div>

      {/* Location */}
      <div className="form-group">
        <label htmlFor="location">Location:</label>
        <input
          type="text"
          id="location"
          name="location"
          value={formData.location}
          onChange={handleChange}
          required
        />
      </div>

      {/* Demographics */}
      <div className="form-group">
        <label htmlFor="demographics">Demographics:</label>
        <select
          id="demographics"
          name="demographics"
          value={formData.demographics}
          onChange={handleChange}
          required
        >
          <option value="Urban">Urban</option>
          <option value="Rural">Rural</option>
          <option value="Suburban">Suburban</option>
        </select>
      </div>

      {/* Profession */}
      <div className="form-group">
        <label htmlFor="profession">Profession:</label>
        <input
          type="text"
          id="profession"
          name="profession"
          value={formData.profession}
          onChange={handleChange}
          required
        />
      </div>

      {/* Income */}
      <div className="form-group">
        <label htmlFor="income">Income ($ USD):</label>
        <input
          type="number"
          id="income"
          name="income"
          value={formData.income}
          onChange={handleChange}
          min="0"
          required
        />
      </div>

      {/* indebt */}
      <div className="form-group checkbox-group">
        <input
          type="checkbox"
          id="indebt"
          name="indebt"
          checked={formData.indebt}
          onChange={handleChange}
        />
        <label htmlFor="indebt">Are you in debt?</label>
      </div>

      {/* isHomeOwner */}
      <div className="form-group checkbox-group">
        <input
          type="checkbox"
          id="isHomeOwner"
          name="isHomeOwner"
          checked={formData.isHomeOwner}
          onChange={handleChange}
        />
        <label htmlFor="isHomeOwner">Are you a home owner?</label>
      </div>

      {/* OwnsCar */}
      <div className="form-group checkbox-group">
        <input
          type="checkbox"
          id="ownsCar" 
          name="ownsCar"
          checked={formData.ownsCar}
          onChange={handleChange}
        />
        <label htmlFor="ownsCar">Do you own a car?</label>
      </div>


      <button type="submit" disabled={loading} className="predict-button">
        {loading ? 'Predicting...' : 'Get Prediction'}
      </button>
    </form>
    </>
  );
}

export default PredictionForm;