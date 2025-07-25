import i18n from '../i18n/i18n';
import { axiosInstance } from './api';

export type ProductTranslationDto = {
  language: string;
  name: string;
  description?: string;
};

export type ProductPayloadDto = {
  price: number;
  stock: number;
  active: boolean;
  mainImageIndex: number;
  categories: string[];
  translation: ProductTranslationDto;
};

export const fetchProducts = async () => {
  const response = await axiosInstance.get('/products', {
    params: { lang: i18n.language.toUpperCase() },
  });
  return response.data;
};

export const fetchProductBySlug = async (slug: string) => {
  const response = await axiosInstance.get(`/products/${slug}`, {
    params: { lang: i18n.language.toUpperCase() },
  });
  return response.data;
};

export const fetchRandomProducts = async (lang: string) => {
  const response = await axiosInstance.get('/products/random', {
    params: { lang: lang.toUpperCase() },
  });
  return response.data;
};

export const fetchRandomProductsByCategory = async (slug: string, lang: string) => {
  const response = await axiosInstance.get('/products/random-by-category', {
    params: {
      slug,
      lang: lang.toUpperCase(),
    },
  });
  return response.data;
};

export const createProduct = async (
  productPayload: ProductPayloadDto,
  imageFiles: File[]
) => {
  const token = localStorage.getItem('adminToken');

  const formData = new FormData();
  formData.append('product', new Blob([JSON.stringify(productPayload)], { type: 'application/json' }));

  if (imageFiles && imageFiles.length > 0) {
    imageFiles.forEach((file) => {
      formData.append('files', file);
    });
  }

  const response = await axiosInstance.post('/products', formData, {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data;
};

export const updateProduct = async (
  slug: string,
  productPayload: ProductPayloadDto,
  imageFiles: File[]
) => {
  const token = localStorage.getItem('adminToken');

  const formData = new FormData();
  formData.append('product', new Blob([JSON.stringify(productPayload)], { type: 'application/json' }));

  if (imageFiles && imageFiles.length > 0) {
    imageFiles.forEach((file) => {
      formData.append('files', file);
    });
  }

  const response = await axiosInstance.put(`/products/${slug}`, formData, {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'multipart/form-data',
    },
  });

  return response.data;
};

export const deleteProduct = async (slug: string) => {
  const token = localStorage.getItem('adminToken');

  const response = await axiosInstance.delete(`/products/${slug}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  return response.data;
};


