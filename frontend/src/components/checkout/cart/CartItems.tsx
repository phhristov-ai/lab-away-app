import React from 'react';
import CartItemRow from './CartItemRow';
import './CartItems.css';
import { useTranslation } from 'react-i18next';

type CartItemsProps = {
  items: (CartItem & {
    image?: string;
    product?: string;
    title?: string;
    subtotal?: number;
    inclVat?: string;
    slug?: string;
  })[];
};

const CartItems: React.FC<CartItemsProps> = ({ items }) => {
  const { t } = useTranslation();

  return (
    <div className="cart-items">
      <h2>{t('checkout.cart.title')}</h2>
      <table>
        <thead>
          <tr>
            <th colSpan={4}></th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <CartItemRow key={item.slug} item={item} />
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default CartItems;
