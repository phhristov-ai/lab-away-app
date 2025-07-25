import React, { useEffect, useMemo, useState } from 'react';
import './ShopPage.css';
import Breadcrumb from '../components/shop/Breadcrumb';
import ShopHeader from '../components/shop/ShopHeader';
import FilterBar from '../components/shop/FilterBar';
import ProductGrid from '../components/shop/ProductGrid';
import { fetchProducts } from '../services/productService';
import { Category, fetchCategories } from '../services/categoriesService';
import { ProductPreviewType } from '../types/ProductPreviewType';
import { useTranslation } from 'react-i18next';

const transformProducts = (backendProducts: any[]): ProductPreviewType[] => {
  return backendProducts.map(product => ({
    name: product.name,
    price: product.price,
    thumbnailUrl: product.thumbnailUrl,
    slug: product.slug,
    categories: product.categories ?? [],
  }));
};

const ShopPage: React.FC = () => {
  const [products, setProducts] = useState<ProductPreviewType[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const { i18n } = useTranslation();

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
    product.categories?.some(category => category.slug === selectedCategory)
  );
}, [products, selectedCategory]);


  return (
    <div className="shop-page">
      <Breadcrumb />
      <ShopHeader />
      <FilterBar
        categories={categories}
        selectedCategory={selectedCategory}
        onSelectCategory={setSelectedCategory}
      />
      {loading ? <p>Loading...</p> : <ProductGrid products={filteredProducts} />}
    </div>
  );
};

export default ShopPage;
