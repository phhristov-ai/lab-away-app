import React from 'react';
import { useShopPage } from '../hooks/useShopPage';
import ShopHeader from '../components/shop/ShopHeader';
import FilterBar from '../components/shop/FilterBar';
import ProductGrid from '../components/shop/ProductGrid';
import ProductGridSkeleton from '../components/shop/ProductGridSkeleton';


const ShopPage: React.FC = () => {
  const {
    categories,
    selectedCategory,
    setSelectedCategory,
    filteredProducts,
    loading,
  } = useShopPage();
  console.log('Rendering ShopPage');
  const categoriesLoaded = categories.length > 0;

  return (
    <div className="shop-page">
      <ShopHeader />

      {categoriesLoaded && (
        <FilterBar
          categories={categories}
          selectedCategory={selectedCategory}
          onSelectCategory={setSelectedCategory}
        />
      )}
      {loading ? (
        <ProductGridSkeleton />
      ) : (
        <ProductGrid products={filteredProducts} showCreateNew />
      )}
    </div>
  );
};

export default ShopPage;
