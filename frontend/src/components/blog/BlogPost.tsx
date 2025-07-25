import React from 'react';
import './BlogPost.css';
import { Link } from 'react-router-dom';
import { Category } from '../../services/categoriesService';
import { formatDate } from '../../utils/format';
import { useTranslation } from 'react-i18next';

type BlogPostProps = {
  title: string;
  content: string;
  image: string;
  slug: string;
  readingTime: number;
  date: string;
  categories: Category[];
};

const BlogPost: React.FC<BlogPostProps> = ({ title, content, image, slug, readingTime, date, categories }) => {
  const formatReadingTime = (minutes: number) => {
    return `${minutes} Min read`;
  };
  const { i18n } = useTranslation();

  return (
    <div className="blog-post">
      <div>
        <Link
          to={`/blog/${slug}`}
          state={{ title, content, image, slug, readingTime, date, categories }}
          className="blog-post-link">
          <img src={image} alt={title} className="blog-post-image" />
          <h2 className="blog-post-title">{title}</h2>
        </Link>
      </div>
      <div className="blog-post-meta">
        {categories && categories.length > 0 && categories.map((cat) => (
          <Link key={cat.slug} to={`/category/${cat.slug}`} className="blog-post-category">
            {cat.name}
          </Link>
        ))}
        <span className="blog-post-date">{formatDate(date, i18n.language)}</span>
        <span className="blog-post-reading-time">{formatReadingTime(readingTime)}</span>
      </div>
    </div>
  );
};

export default BlogPost;