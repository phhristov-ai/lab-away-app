import React from 'react';
import './BlogPost.css';
import { Link } from 'react-router-dom';
import { Category } from '../../services/categoriesService';
import { formatDate } from '../../utils/format';
import { useTranslation } from 'react-i18next';

type BlogPostProps = {
  title: string;
  content: string;
  imageUrls: {
    small: string;
    medium: string;
    large: string;
  };
  slug: string;
  categories: Category[];
  readingTime: number;
  date: string;
};

const BlogPost: React.FC<BlogPostProps> = ({
  title,
  content,
  imageUrls,
  slug,
  readingTime,
  date,
  categories,
}) => {
  const { i18n } = useTranslation();

  const formatReadingTime = (minutes: number) => `${minutes} Min read`;

  return (
    <div className="blog-post">
      <div>
        <Link
          to={`/blog/${slug}`}
          state={{
            title,
            content,
            imageUrls,
            slug,
            readingTime,
            date,
            categories,
          }}
          className="blog-post-link"
        >
        <img
          className="blog-post-image"
          src={imageUrls.small}
          srcSet={`
            ${imageUrls.small} 480w,
            ${imageUrls.medium} 768w,
            ${imageUrls.large} 1200w
          `}
          sizes="(max-width: 768px) 100vw, 300px"
          alt={title}
          loading="lazy"
        />
          <h2 className="blog-post-title">{title}</h2>
        </Link>
      </div>

      <div className="blog-post-meta">
        {categories?.map((cat) => (
          <Link
            key={cat.slug}
            to={`/category/${cat.slug}`}
            className="blog-post-category"
          >
            {cat.name}
          </Link>
        ))}

        <span className="blog-post-date">
          {formatDate(date, i18n.language)}
        </span>

        <span className="blog-post-reading-time">
          {formatReadingTime(readingTime)}
        </span>
      </div>
    </div>
  );
};

export default BlogPost;
