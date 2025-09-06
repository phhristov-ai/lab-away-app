import React from 'react';
import './ProductDetails.css';
import AddToCartButton from '../product/AddToCartButton';
import { useTranslation } from 'react-i18next';
import { formatCurrency } from '../../utils/format';
import { useNavigate } from 'react-router-dom';

type ProductDetailsProps = {
  name: string;
  price: number;
  image: string;
  slug: string;
};

const ProductDetails: React.FC<ProductDetailsProps> = ({ name, price, image, slug }) => {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();

  const handleAddToCart = () => {
    navigate('/cart');
  };

  return (
    <div className="product-details">
      <h3>{name}</h3>
      <p>{formatCurrency(price, i18n.language)}</p>
      <AddToCartButton
        onClick={handleAddToCart}
        label={t('productPage.buttons.addToCart')}
        slug={slug}
        name={name}
        image={image}
        price={price}
        quantity={1}
      />
    </div>
  );
};


export default ProductDetails;
