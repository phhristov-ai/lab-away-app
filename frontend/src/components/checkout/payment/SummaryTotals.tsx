import React from 'react';
import { useTranslation } from 'react-i18next';
import { formatCurrency } from '../../../utils/format';

type Props = {
  subtotal: number;
  shippingCost: number;
  total: number;
};

const SummaryTotals: React.FC<Props> = ({ subtotal, shippingCost, total }) => {
  const { t, i18n } = useTranslation();
  return (
    <>
      <tr className="order-summary-totals">
        <td><strong>{t('checkout.summary.subtotal')}</strong></td>
        <td></td>
        <td className="order-summary-price">{formatCurrency(subtotal, i18n.language)}</td>
      </tr>
      <tr className="order-summary-totals">
        <td><strong>{t('checkout.summary.shipping')}</strong></td>
        <td></td>
        <td className="order-summary-price">{formatCurrency(shippingCost, i18n.language)}</td>
      </tr>
      <tr className="order-summary-total">
        <td><strong>{t('checkout.summary.total')}</strong></td>
        <td></td>
        <td className="order-summary-price">{formatCurrency(total, i18n.language)}</td>
      </tr>
    </>
  );
};

export default SummaryTotals;
