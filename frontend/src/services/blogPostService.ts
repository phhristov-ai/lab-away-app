import { axiosInstance } from "./api";
import { Category } from "./categoriesService";

export type BlogImageUrls = {
  small: string;
  medium: string;
  large: string;
};

export type BlogPostType = {
  slug: string;
  author: string;
  imageUrls: BlogImageUrls;
  title: string;
  content: string;
  excerpt: string;
  readingTime: number;
  createdAt: string;
  updatedAt: string;
  categories: Category[];
};

export const fetchBlogPosts = async (lang: string) => {
  const response = await axiosInstance.get('/blogs', {
    params: { lang },
  });
  return response.data;
};

export const fetchBlogPost = async (slug: string, lang: string) => {
  const response = await axiosInstance.get(`/blogs/${slug}`, {
    params: { lang },
  });
  return response.data;
};

export const fetchRandomBlogs = async (lang: string) => {
  const response = await axiosInstance.get('/blogs/random', {
    params: { lang: lang.toUpperCase() },
  });
  return response.data;
};

const sendBlogRequest = async (
  {
    slug,
    author,
    categories,
    title,
    content,
    imageFile,
  }: {
    slug?: string;
    author: string;
    categories: string[];
    title: string;
    content: string;
    imageFile?: File;
  },
  language: string,
  method: 'post' | 'put'
) => {
  const token = localStorage.getItem('adminToken');

  const formData = new FormData();

  const blogPayload = {
    author,
    categorySlugs: categories,
    translation: {
      language,
      title,
      content,
    },
  };

  formData.append(
    'blog',
    new Blob([JSON.stringify(blogPayload)], {
      type: 'application/json',
    })
  );

  if (imageFile) {
    formData.append('file', imageFile);
  }

  const url = slug ? `/blogs/${slug}` : `/blogs`;

  const response = await axiosInstance.request({
    method,
    url,
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data',
      Authorization: `Bearer ${token}`,
    },
    params: {
      lang: language,
    },
  });

  return response.data;
};

export const saveBlogPost = async (
  args: {
    author: string;
    categories: string[];
    title: string;
    content: string;
    imageFile?: File;
  },
  language: string
) => sendBlogRequest({ ...args }, language, 'post');

export const updateBlogPost = async (
  args: {
    slug: string;
    author: string;
    categories: string[];
    title: string;
    content: string;
    imageFile?: File;
  },
  language: string
) => sendBlogRequest({ ...args }, language, 'put');



export const deleteBlogPost = async (slug: string) => {
  const token = localStorage.getItem('adminToken');

  const response = await axiosInstance.delete(`/blogs/${slug}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
};


