import React from 'react';
import './ExternalResources.css';

const ExternalResources = () => {
  return (
    <div className="external-resources-container message-container visible">
      <h3>Further Support & Resources:</h3>
      <ul>
        <li>
          <a href="https://internetaddictsanonymous.org/" target="_blank" rel="noopener noreferrer">
            Internet and Technology Addicts Anonymous (ITAA)
          </a> - A 12-step fellowship for recovery from internet and technology addiction.
        </li>
        <li>
          <a href="https://www.helpguide.org/mental-health/wellbeing/social-media-and-mental-health" target="_blank" rel="noopener noreferrer">
            HelpGuide.org: Social Media and Mental Health
          </a> - Comprehensive guide with tips for healthier social media use.
        </li>
        <li>
          <a href="https://www.psychiatry.org/patients-families/technology-addictions-social-media-and-more" target="_blank" rel="noopener noreferrer">
            Psychiatry.org: Technology Addictions
          </a> - Information from the American Psychiatric Association.
        </li>
        <li className="crisis-line">
          If you are in crisis, please call or text the Suicide & Crisis Lifeline at <a href="tel:988">988</a> (US).
        </li>
      </ul>
    </div>
  );
};

export default ExternalResources;