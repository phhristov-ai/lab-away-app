import React, { useEffect, useState } from 'react';
import BlogPostGrid from '../components/blog/BlogPostGrid';
import { BlogPostType, fetchBlogPosts } from '../services/blogPostService';
import { useTranslation } from 'react-i18next';
import './BlogPage.css';

const BlogPage: React.FC = () => {
  const { t, i18n } = useTranslation();
  const [posts, setPosts] = useState<BlogPostType[]>([]);

  useEffect(() => {
    if (!i18n.language) return;

    fetchBlogPosts(i18n.language.toUpperCase())
      .then(data => {
        const mapped = data.map((post: any) => ({ ...post, image: post.imageUrl }));
        setPosts(mapped);
      })
  }, [i18n.language]);


  return (
    <div>
      <h1 className="center-title">{t('blog.title')}</h1>
      <BlogPostGrid blogPosts={posts} />
    </div>
  );
};

export default BlogPage;