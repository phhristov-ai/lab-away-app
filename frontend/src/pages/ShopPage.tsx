import React from 'react';
import { useTranslation } from 'react-i18next';
import { useShopPage } from '../hooks/useShopPage';
import Breadcrumb from '../components/shop/Breadcrumb';
import ShopHeader from '../components/shop/ShopHeader';
import FilterBar from '../components/shop/FilterBar';
import ProductGrid from '../components/shop/ProductGrid';


const ShopPage: React.FC = () => {
  const { t } = useTranslation();
  const {
    categories,
    selectedCategory,
    setSelectedCategory,
    filteredProducts,
    loading,
  } = useShopPage();

  return (
    <div className="shop-page">
      <Breadcrumb
        items={[
          { label: t('shop.breadcrumb.home'), to: '/' },
          { label: t('shop.breadcrumb.certifiedTests') },
        ]}
      />
      <ShopHeader />
      <FilterBar
        categories={categories}
        selectedCategory={selectedCategory}
        onSelectCategory={setSelectedCategory}
      />
      {loading ? <p>Loading...</p> : <ProductGrid products={filteredProducts} showCreateNew />}
    </div>
  );
};

export default ShopPage;
