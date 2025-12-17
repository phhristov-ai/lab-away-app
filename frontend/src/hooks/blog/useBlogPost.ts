import { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAdmin } from '../../context/AdminContext';
import { Category, fetchCategories } from '../../services/category/categoriesService';
import { BlogPostType, deleteBlogPost, fetchBlogPost, fetchRandomBlogs, saveBlogPost, updateBlogPost } from '../../services/blog/blogPostService';
import { trackScrollBlogPost, trackViewBlogPost } from '../../utils/analytics';

export function useBlogPost() {
  const { slug } = useParams();
  const location = useLocation();
  const { i18n } = useTranslation();
  const { isAdmin } = useAdmin();

  const [post, setPost] = useState<any>(location.state ?? null);
  const [isFullPostLoaded, setIsFullPostLoaded] = useState(false);
  const [allCategories, setAllCategories] = useState<Category[]>([]);
  const [selectedCategories, setSelectedCategories] = useState<Category[]>(post?.categories ?? []);
  const [editedTitle, setEditedTitle] = useState(post?.title ?? '');
  const [editedContent, setEditedContent] = useState(post?.content ?? '');
  const navigate = useNavigate();
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [randomPosts, setRandomPosts] = useState<BlogPostType[]>([]);
  const [showConfirmDelete, setShowConfirmDelete] = useState(false);
  const handleDeleteClick = () => {
    setShowConfirmDelete(true);
  };

  const handleConfirmDelete = async () => {
    try {
      await deletePost();
    } catch {
    } finally {
      setShowConfirmDelete(false);
    }
  };

  useEffect(() => {
    if (post?.slug && isFullPostLoaded && !isAdmin) {
      trackViewBlogPost(post.slug, post.title, post.categories, post.author);
    }
  }, [post, isFullPostLoaded, isAdmin]);

  useEffect(() => {
    if (slug === 'new' && isAdmin) {
      setPost(null);
      setEditedTitle('');
      setEditedContent('');
      setSelectedCategories([]);
      setIsFullPostLoaded(true);
    } else if (slug && slug !== 'new') {
      setIsFullPostLoaded(false);
    }
  }, [slug, isAdmin]);

  // Language change
  useEffect(() => {
    setIsFullPostLoaded(false);
  }, [slug, i18n.language]);

  // Fetch post
  useEffect(() => {
    if (slug && !isFullPostLoaded && i18n.language) {
      fetchBlogPost(slug, i18n.language.toUpperCase())
        .then((data: { title: any; content: any; }) => {
          setPost(data);
          setEditedTitle(data.title);
          setEditedContent(data.content);
          setIsFullPostLoaded(true);
        })
        .catch(console.error);
    }
  }, [slug, isFullPostLoaded, i18n.language]);

  // Fetch categories
  useEffect(() => {
    fetchCategories().then(setAllCategories).catch(console.error);
  }, [i18n.language]);

  // Update selected categories
  useEffect(() => {
    if (post?.categories) setSelectedCategories(post.categories);
  }, [post?.categories]);

  const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedOptions = Array.from(e.target.selectedOptions);
    const selected = selectedOptions.map(opt => {
      const cat = allCategories.find(c => c.slug === opt.value);
      return cat!;
    });
    setSelectedCategories(selected);
  };

  const handleSave = async () => {
    try {
      const isNew = slug === 'new';
      const language = i18n.language.toUpperCase();

      let result;

      if (isNew) {
        result = await saveBlogPost(
          {
            author: 'admin',
            categories: selectedCategories.map((cat) => cat.slug),
            title: editedTitle,
            content: editedContent,
            imageFile: imageFile ?? undefined,
          },
          language
        );
      } else {
        if (!slug) {
          throw new Error('Slug is required for update');
        }
        result = await updateBlogPost(
          {
            slug,
            author: 'admin',
            categories: selectedCategories.map((cat) => cat.slug),
            title: editedTitle,
            content: editedContent,
            imageFile: imageFile ?? undefined,
          },
          language
        );
      }

      const updatedSlug = result.slug;

      setPost((prev: any) => ({
        ...prev,
        title: editedTitle,
        content: editedContent,
        categories: selectedCategories,
      }));

      setSuccessMessage(isNew ? 'Post created successfully!' : 'Post updated successfully!');
      setShowSuccessModal(true);

      setTimeout(() => {
        navigate(`/blog/${updatedSlug}`);
      }, 1500);
    } catch (err) {
      console.error('Error saving blog post:', err);
    }
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setImageFile(file);
      setImagePreview(URL.createObjectURL(file));
    }
  };

  const deletePost = async () => {
    if (!post?.slug) return;

    try {
      await deleteBlogPost(post.slug);
      navigate('/blog');
    } catch (err) {
      console.error('Error deleting post:', err);
      throw err;
    }
  };

  useEffect(() => {
    const loadRandomPosts = async () => {
      try {
        const posts = await fetchRandomBlogs(i18n.language);
        setRandomPosts(posts);
      } catch (error) {
        console.error('Failed to fetch random posts:', error);
      }
    };

    loadRandomPosts();
  }, [i18n.language]);



  function useBlogScrollTracking(slug: string, title: string) {
    useEffect(() => {
      console.log(title);
      console.log(slug);

      if (!slug || !title) return;

      const scrollDepths = [25, 50, 75, 100];
      const triggered = new Set<number>();

      function handleScroll() {
        const scrollTop = window.scrollY;
        const docHeight = document.documentElement.scrollHeight - window.innerHeight;
        const scrollPercent = (scrollTop / docHeight) * 100;

        scrollDepths.forEach((depth) => {
          if (scrollPercent >= depth && !triggered.has(depth)) {
            triggered.add(depth);
            trackScrollBlogPost(slug, title, depth);
          }
        });

        // Stop listening once all thresholds are reached
        if (triggered.size === scrollDepths.length) {
          window.removeEventListener('scroll', handleScroll);
        }
      }

      // Debounce to prevent excessive firing
      let timeout: ReturnType<typeof setTimeout>;
      const onScroll = () => {
        clearTimeout(timeout);
        timeout = setTimeout(handleScroll, 200);
      };

      window.addEventListener('scroll', onScroll);
      return () => {
        window.removeEventListener('scroll', onScroll);
        clearTimeout(timeout);
      };
    }, [slug, title]);
  }

  return {
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
  };
}
