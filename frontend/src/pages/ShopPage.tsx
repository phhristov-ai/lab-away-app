import React from 'react';
import { useShopPage } from '../hooks/useShopPage';
import ShopHeader from '../components/shop/ShopHeader';
import FilterBar from '../components/shop/FilterBar';
import ProductGrid from '../components/shop/ProductGrid';
import ProductGridSkeleton from '../components/shop/ProductGridSkeleton';
import { Helmet } from 'react-helmet';


const ShopPage: React.FC = () => {
  const {
    categories,
    selectedCategory,
    setSelectedCategory,
    filteredProducts,
    loading,
  } = useShopPage();
  const categoriesLoaded = categories.length > 0;

  return (
    <div className="shop-page">

      <Helmet>
        <title>Shop - Lab-Away | High-Quality Home Test Kits</title>
        <meta 
          name="description" 
          content="Shop a wide variety of lab-grade home test kits from Lab-Away. Drug tests, fertility tests, STI/STD tests, and more. Fast and discreet delivery!" 
        />
        
        <script type="application/ld+json">
          {`
            {
              "@context": "https://schema.org",
              "@type": "WebPage",
              "name": "Lab-Away Shop",
              "description": "Shop lab-grade home test kits including drug tests, fertility tests, STI/STD tests, and more. Get fast and discreet delivery.",
              "url": "https://www.lab-away.com/shop"
            }
          `}
        </script>
      </Helmet>
      
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
