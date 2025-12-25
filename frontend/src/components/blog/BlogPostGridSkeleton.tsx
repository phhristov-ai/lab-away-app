import React from 'react';
import './BlogPostGridSkeleton.css';

const BlogPostGridSkeleton: React.FC = () => {
  return (
    <div className="blog-post-grid">
      {Array.from({ length: 6 }).map((_, index) => (
        // eslint-disable-next-line react/no-array-index-key
        <div key={index} className="blog-post-item">
          <div className="skeleton-card">
            <div className="skeleton-image" />
            <div className="skeleton-text title" />
            <div className="skeleton-text excerpt" />
          </div>
        </div>
      ))}
    </div>
  );
};

export default BlogPostGridSkeleton;
