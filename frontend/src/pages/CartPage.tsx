import React from 'react';
import CartItems from '../components/checkout/cart/CartItems';
import CartTotals from '../components/checkout/cart/CartTotals';
import CheckoutLayout from '../layouts/CheckoutLayout';
import CustomButton from '../components/checkout/billing/CustomButton';
import { useNavigate } from 'react-router-dom';
import './CartPage.css';
import { useCart } from '../context/CartContext';
import { useTranslation } from 'react-i18next';

const CartPage: React.FC = () => {
  const navigate = useNavigate();
  const { state } = useCart();
  const { t } = useTranslation();

  const enrichedItems = state.items.map(item => ({
    ...item,
    inclVat: t('checkout.cart.inclVat'),
    subtotal: item.price * item.quantity,
    title: item.name,
    product: item.name,
  }));

  const subtotalValue = state.items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  const vat = 3; 
  const shippingCost = 7;
  const total = subtotalValue + shippingCost;

  return (
    <CheckoutLayout>
      <div>
        <div className="cart-layout">
          <div className="cart-main">
            <CartItems items={enrichedItems} />
          </div>
          <div className="cart-sidebar">
          <CartTotals
            subtotal={subtotalValue}
            vat={vat}
            total={total}
            shipping={shippingCost}
            labels={{
              cartTotalsTitle: t('checkout.cartTotals.title'),
              description: t('checkout.cartTotals.description'),
              amount: t('checkout.cartTotals.amount'),
              shipping: t('checkout.summary.shipping'),
              total: t('checkout.summary.total'),
              vatNotePrefix: t('checkout.cartTotals.vatNotePrefix'),
              vatNoteSuffix: t('checkout.cartTotals.vatNoteSuffix'),
            }}
          />
            <div className="cart-button-container">
              <CustomButton
                label={t('checkout.actions.continue')}
                onClick={() => navigate('/checkout')}
                variant="third"
              />
            </div>
          </div>
        </div>
      </div>
    </CheckoutLayout>
  );
};

export default CartPage;