import { useEffect, useState } from "react";
import { fetchRandomProducts, fetchRandomProductsByCategory } from "../../../services/product/productService";
import ProductGrid from "../../shop/ProductGrid";
import { ProductPreviewType } from "../../../types/ProductPreviewType";
import { useTranslation } from 'react-i18next';
import './RandomProducts.css';
import ProductGridSkeleton from "../../shop/ProductGridSkeleton";
import { transformProducts } from "../../../utils/productPreview.mapper";

type RandomProductsProps = {
  direction?: 'row' | 'column';
  categorySlug?: string;
  handleProductClick?: (product: ProductPreviewType) => void;
};

const RandomProducts: React.FC<RandomProductsProps> = ({ direction = 'row', categorySlug, handleProductClick }) => {
  const [products, setProducts] = useState<ProductPreviewType[]>([]);
  const [loading, setLoading] = useState(true);
  const { i18n } = useTranslation();

  useEffect(() => {
    const loadRandom = async () => {
      setLoading(true);
      try {
        const lang = i18n.language;
        const result = categorySlug
          ? await fetchRandomProductsByCategory(categorySlug, lang)
          : await fetchRandomProducts(lang);
        setProducts(result);
      } catch (err) {
        console.error('Error loading random products:', err);
        setProducts([]);
      } finally {
        setLoading(false);
      }
    };

    loadRandom();
  }, [i18n.language, categorySlug]);

  if (loading) {
    return <ProductGridSkeleton />;
  }

  if (!products.length) {
    return <p>No products available.</p>;
  }

  return (
    <div className={`random-products-section ${direction}`}>
      <ProductGrid products={transformProducts(products)} onProductClick={handleProductClick} />
    </div>
  );
};

export default RandomProducts;