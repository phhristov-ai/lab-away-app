import React from 'react';
import { useTranslation } from 'react-i18next';
import { formatCurrency } from '../../../utils/format';
import { t } from 'i18next';

type CartTotalsProps = {
  subtotal: number;
  vat: number;
  total: number;
  shipping: number;
  labels: {
    cartTotalsTitle: string;
    description: string;
    amount: string;
    subtotal: string;
    shipping: string;
    total: string;
    vatNotePrefix: string;
    vatNoteSuffix: string;
  };
};

const CartTotals: React.FC<CartTotalsProps> = ({
  vat,
  total,
  shipping,
  labels,
  subtotal
}) => {
  const { i18n } = useTranslation();
  const locale = i18n.language;

  return (
    <div className="cart-totals">
      <h2>{labels.cartTotalsTitle}</h2>
      <table className="totals-table mobile-card-table">
        <thead>
          <tr>
            <th></th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{labels.subtotal}</td>
            <td>{formatCurrency(subtotal, locale)}</td>
          </tr>
          <tr>
            <td>{labels.shipping}</td>
            <td>
              {shipping === 0
                ? t('checkout.cart.freeShipping')
                : formatCurrency(shipping, locale)}
            </td>
          </tr>
          <tr>
            <td>{labels.total}</td>
            <td>
              {formatCurrency(total, locale)}{' '}
              <sub className="vat-note">
                {labels.vatNotePrefix}
              </sub>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
};

export default CartTotals;
