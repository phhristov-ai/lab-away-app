import React from 'react';
import { Link } from 'react-router-dom';
import './NotFound.css'; // Import your CSS file

const NotFound: React.FC = () => {
  return (
    <div className="notfound-container">
      <h1 className="notfound-title">404 - Page Not Found</h1>
      <p className="notfound-text">
        Oops! The page you are looking for does not exist.
      </p>
      <Link to="/" className="notfound-link">
        Go back home
      </Link>
    </div>
  );
};

export default NotFound;
