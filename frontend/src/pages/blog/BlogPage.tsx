import React, { useEffect, useState } from 'react';
import BlogPostGrid from '../../components/blog/BlogPostGrid';
import { BlogPostType, fetchBlogPosts } from '../../services/blog/blogPostService';
import { useTranslation } from 'react-i18next';
import './BlogPage.css';
import BlogPostGridSkeleton from '../../components/blog/BlogPostGridSkeleton';
import { trackViewBlog } from '../../utils/analytics';
import { Helmet } from 'react-helmet';

const BlogPage: React.FC = () => {
  const { t, i18n } = useTranslation();
  const [posts, setPosts] = useState<BlogPostType[]>([]);
  const [loading, setLoading] = React.useState(true);

  useEffect(() => {
    trackViewBlog();
  }, []);

  useEffect(() => {
    if (!i18n.language) return;

    setLoading(true);
    fetchBlogPosts(i18n.language.toUpperCase())
      .then(data => {
        const mapped = data.map((post: any) => ({ ...post, imageUrls: post.imageUrls }));
        setPosts(mapped);
      })
      .finally(() => setLoading(false));
  }, [i18n.language]);

  return (
    <div>

      {/* SEO Setup using Helmet */}
      <Helmet>
        <title>{t('blog.title')} - Lab-Away</title>
        <meta 
          name="description" 
          content="Read insightful articles about health, drug tests, fertility tests, and more at Lab-Away's blog. Stay informed and stay healthy." 
        />

        {/* Structured Data for Blog Page */}
        <script type="application/ld+json">
          {`
            {
              "@context": "https://schema.org",
              "@type": "WebPage",
              "name": "Lab-Away Blog",
              "description": "Explore Lab-Away's blog for health tips, insights on drug tests, fertility tests, and more. Stay informed with expert advice.",
              "url": "https://www.lab-away.com/blog"
            }
          `}
        </script>
      </Helmet>
      <h1 className="center-title">{t('blog.title')}</h1>
      {loading ? <BlogPostGridSkeleton /> : <BlogPostGrid blogPosts={posts} />}
    </div>
  );
};

export default BlogPage;