import RandomProducts from '../components/common/RandomProducts';
import { useBlogPost } from '../hooks/useBlogPost';
import './BlogPostPage.css';
import { formatDate } from '../utils/format';
import AdminActionButtons from '../components/admin/AdminActionButtons';
import CategorySelect from '../components/admin/CategorySelect';

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
    i18n
  } = useBlogPost();

  if (slug === "new" && !isAdmin) {
    return <div>Access denied.</div>;
  }

  return (
    <div className="blog-post-container">
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
          <div
            onClick={() => document.getElementById('image-upload')?.click()}
            style={{
              cursor: 'pointer',
              width: 675,
              height: 450,
              backgroundColor: '#eee',
              borderRadius: 8,
              margin: '1rem 0',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              position: 'relative',
            }}
          >
            {imagePreview || post?.image || location.state?.image ? (
              <img
                src={imagePreview ?? post?.image ?? location.state?.image}
                alt="Blog preview"
                style={{
                  width: 675,
                  height: 450,
                  objectFit: 'cover',
                }}
              />
            ) : (
              <span style={{ color: '#999', fontSize: '1.25rem' }}>Click to add image</span>
            )}
            <input
              type="file"
              id="image-upload"
              accept="image/*"
              onChange={handleImageChange}
              style={{ display: 'none' }}
            />
          </div>
        ) : (
          (post?.image ?? location.state?.image) && (
            <img
              src={post.image ?? location.state?.image}
              alt={post.title}
              style={{
                width: '100%',
                maxHeight: 400,
                objectFit: 'cover',
                borderRadius: 8,
                margin: '1rem 0',
              }}
            />
          )
        )}

        {isAdmin ? (
          <textarea
            placeholder="Enter post content here..."
            value={editedContent}
            onChange={(e) => setEditedContent(e.target.value)}
            rows={15}
            style={{ width: '100%', fontSize: '1.1rem', marginTop: '1rem' }}
          />
        ) : (
          <div
            className="blog-post-content"
            dangerouslySetInnerHTML={{ __html: post?.content ?? location.state?.content }}
          />)}


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

      {post && (
        <aside className="blog-post-sidebar">
          <RandomProducts direction="column" categorySlug={post.categories?.[0]?.slug} />
        </aside>
      )}
    </div>
  );
};

export default BlogPostPage;