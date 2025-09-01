import React from 'react';
import { Link } from 'react-router-dom';
import ShoppingCart from '../../assets/images/checkout/cart-shopping-solid.svg';
import './CartIcon.css';
import { useTranslation } from 'react-i18next';

interface CartIconProps {
  cartCount: number;
}

const CartIcon: React.FC<CartIconProps> = ({ cartCount }) => {
  const { t } = useTranslation();

  return (
    <Link to="/cart" className="cart-icon-link">
      <div className="cart-icon">
        <span className="thb-item-text">
          <img
            src={ShoppingCart}
            alt={t('header.cart.altText')}
            className="cart-img"
          />
        </span>
        <div className="thb-item-icon-wrapper">
          <span className="count thb-cart-count">
            {`(${cartCount})`}
          </span>
        </div>
      </div>
    </Link>
  );
};

export default CartIcon;