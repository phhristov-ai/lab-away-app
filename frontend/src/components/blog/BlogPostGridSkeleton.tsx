import React from 'react';
import './BlogPostGridSkeleton.css';
import { nanoid } from 'nanoid';

const BlogPostGridSkeleton: React.FC = () => {
  const skeletons = Array.from({ length: 6 }, () => ({ id: nanoid() }));

  return (
    <div className="blog-post-grid">
      {skeletons.map((skeleton) => (
        <div key={skeleton.id} className="blog-post-item">
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
