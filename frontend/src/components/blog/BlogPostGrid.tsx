import React from 'react';
import './BlogPostGrid.css';
import BlogPost from './BlogPost';
import { BlogPostType } from '../../services/blogPostService';
import { Link } from 'react-router-dom';
import { useAdmin } from '../../context/AdminContext';

type BlogPostGridProps = {
  blogPosts: BlogPostType[];
};


const BlogPostGrid: React.FC<BlogPostGridProps> = ({ blogPosts }) => {
  const { isAdmin } = useAdmin();

  return (
    <div className="blog-post-grid">
      {isAdmin && (
        <div className="blog-post-item new-post-item">
          <Link to="/blog/new" className="new-blog-post-link">
            <div className="new-blog-post-content">
              <div className="new-post-image-placeholder">+</div>
              <h2>Create New Blog Post</h2>
              <p>Click here to start writing a new post.</p>
            </div>
          </Link>
        </div>
      )}

      {blogPosts.map((post) => (
        <div key={post.slug} className="blog-post-item">
          <Link
            to={`/blog/${post.slug}`}
            state={{
              title: post.title,
              content: post.excerpt,
              image: post.imageUrl,
              slug: post.slug,
              categories: post.categories,
              readingTime: post.readingTime,
              date: post.createdAt,
            }}
          >
            <BlogPost
              title={post.title}
              content={post.excerpt}
              image={post.imageUrl}
              slug={post.slug}
              categories={post.categories}
              readingTime={post.readingTime}
              date={post.createdAt}
            />
          </Link>
        </div>
      ))}
    </div>
  );
};

export default BlogPostGrid;


