import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Category, fetchCategories } from '../../services/category/categoriesService';
import { ProductPreviewType } from '../../types/ProductPreviewType';
import { fetchProducts } from '../../services/product/productService';
import { transformProducts } from '../../utils/productPreview.mapper';

export const useShopPage = () => {
  const [products, setProducts] = useState<ProductPreviewType[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const { i18n } = useTranslation();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const categoryFromUrl = searchParams.get('category');
    if (categoryFromUrl) {
      setSelectedCategory(categoryFromUrl);
    }
  }, [searchParams]);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [backendProducts, backendCategories] = await Promise.all([
          fetchProducts(),
          fetchCategories(),
        ]);
        setProducts(transformProducts(backendProducts));
        setCategories(backendCategories);
      } catch (error) {
        console.error('Failed to load shop data:', error);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [i18n.language]);

  const filteredProducts = useMemo(() => {
    if (!selectedCategory) return products;

    return products.filter(product =>
      product.categories?.some((category: { slug: string; }) => category.slug === selectedCategory)
    );
  }, [products, selectedCategory]);

  return {
    categories,
    selectedCategory,
    setSelectedCategory,
    filteredProducts,
    loading,
  };
};


