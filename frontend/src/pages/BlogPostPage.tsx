import RandomProducts from '../components/common/RandomProducts';
import { useBlogPost } from '../hooks/useBlogPost';
import './BlogPostPage.css';
import './AdminBlogPost.css';
import { formatDate } from '../utils/format';
import AdminActionButtons from '../components/admin/AdminActionButtons';
import CategorySelect from '../components/admin/CategorySelect';
import BlogPostGrid from '../components/blog/BlogPostGrid';
import { trackClickBlogToProduct } from '../utils/analytics';
import { Helmet } from 'react-helmet';

const BlogPostPage: React.FC = () => {
  const {
    slug,
    post,
    isAdmin,
    location,
    allCategories,
    selectedCategories,
    editedTitle,
    editedContent,
    handleCategoryChange,
    handleSave,
    setEditedTitle,
    setEditedContent,
    imagePreview,
    handleImageChange,
    showSuccessModal,
    successMessage,
    setShowSuccessModal,
    showConfirmDelete,
    handleDeleteClick,
    handleConfirmDelete,
    setShowConfirmDelete,
    i18n,
    randomPosts,
    useBlogScrollTracking
  } = useBlogPost();

  useBlogScrollTracking(post?.slug ?? "", post?.title ?? "");

  if (slug !== "new" && !post) {
    return <div>Loading...</div>;
  }

  if (slug === "new" && !isAdmin) {
    return <div>Access denied.</div>;
  }

  const imageUrls = post?.imageUrls ?? location.state?.imageUrls;

  return (
    <div className="blog-post-container">

    {/* Helmet */}
    {slug !== "new" && post && (
      <Helmet>
        <title>{post.title} - Lab-Away Blog</title>
        <meta
          name="description"
          content={
            post.excerpt ||
            "Read the latest blog post on Lab-Away, your trusted health testing platform."
          }
        />
        <meta property="og:title" content={post.title} />
        <meta
          property="og:description"
          content={
            post.excerpt || "Read the latest blog post on Lab-Away."
          }
        />
        <meta
          property="og:url"
          content={`https://www.lab-away.com/blog/${slug}`}
        />
        <meta
          property="og:image"
          content={post.imageUrl || "default-image.jpg"}
        />
      </Helmet>
    )}

      <div className="post-main">
        {isAdmin ? (
          <input
            className="post-input"
            type="text"
            placeholder="Enter title"
            value={editedTitle}
            onChange={(e) => setEditedTitle(e.target.value)}
          />
        ) : (
          <h1>{post?.title}</h1>
        )}

        <div className="blog-post-meta">
          {isAdmin ? (
            <CategorySelect
              allCategories={allCategories}
              selectedSlugs={selectedCategories.map(cat => cat.slug)}
              onChange={handleCategoryChange}
            />
          ) : (
            <span>{post?.categories?.map((cat: any) => cat.name).join(', ')}</span>
          )}

          {post && (
            <>
              <span>{formatDate(post.updatedAt, i18n.language)}</span>
              <span>{post.readingTime} min read</span>
            </>
          )}
        </div>

        {isAdmin ? (
          <button
            type="button"
            className="admin-image-upload"
            onClick={() => document.getElementById('image-upload')?.click()}
          >

            {imagePreview || post?.image || location.state?.image ? (
              <img
                src={imagePreview ?? post?.image ?? location.state?.image}
                alt="Blog preview"
                className="admin-image-preview"
              />
            ) : (
              <span className="admin-image-placeholder">Click to add image</span>
            )}
            <input
              type="file"
              id="image-upload"
              accept="image/*"
              onChange={handleImageChange}
              className="hidden-file-input"
            />
          </button>
        ) : (
          imageUrls && (
            <picture>
              <source media="(max-width: 600px)" srcSet={imageUrls.small} />
              <source media="(max-width: 1024px)" srcSet={imageUrls.medium} />
              <img
                src={imageUrls.large}
                alt={post?.title ?? ""}
                className="blog-post-image"
                loading="lazy"
              />
            </picture>
          )

        )}

        {isAdmin ? (
          <textarea
            placeholder="Enter post content here..."
            value={editedContent}
            onChange={(e) => setEditedContent(e.target.value)}
            rows={15}
            className="admin-post-textarea"
          />
        ) : (
          <div><div
            className="blog-post-content"
            dangerouslySetInnerHTML={{ __html: post?.content ?? location.state?.content }}
          />
            <BlogPostGrid blogPosts={randomPosts} />
          </div>
        )}

        {isAdmin && (
          <AdminActionButtons
            isNew={slug === "new"}
            onSave={handleSave}
            onDelete={handleDeleteClick}
            showConfirmDelete={showConfirmDelete}
            onConfirmDelete={handleConfirmDelete}
            onCancelDelete={() => setShowConfirmDelete(false)}
            showSuccessModal={showSuccessModal}
            successMessage={successMessage}
            onCloseSuccess={() => setShowSuccessModal(false)}
          />
        )}

      </div>
      {!isAdmin && post && (
        <aside className="blog-post-sidebar">
          <RandomProducts direction="column"
            categorySlug={post.categories?.[0]?.slug}
            handleProductClick={(product) =>
              trackClickBlogToProduct(post.slug, post.title, product.slug, product.name)
            } />
        </aside>
      )}
    </div>
  );
};

export default BlogPostPage;