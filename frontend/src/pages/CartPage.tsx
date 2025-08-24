import React from 'react';
import CartItems from '../components/checkout/cart/CartItems';
import CartTotals from '../components/checkout/cart/CartTotals';
import CheckoutLayout from '../layouts/CheckoutLayout';
import CustomButton from '../components/checkout/billing/CustomButton';
import { useNavigate } from 'react-router-dom';
import './CartPage.css';
import { useCartPage } from '../hooks/useCartPage';

const CartPage: React.FC = () => {
  const navigate = useNavigate();

  const {
    enrichedItems,
    subtotalValue,
    vat,
    shippingCost,
    total,
    labels,
  } = useCartPage();

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
                cartTotalsTitle: labels.cartTotalsTitle,
                description: labels.description,
                amount: labels.amount,
                shipping: labels.shipping,
                total: labels.total,
                vatNotePrefix: labels.vatNotePrefix,
                vatNoteSuffix: labels.vatNoteSuffix,
              }}
            />
            <div className="cart-button-container">
              <CustomButton
                label={labels.continue}
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