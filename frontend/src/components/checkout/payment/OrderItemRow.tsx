import React from 'react';
import QuantitySelector from '../../product/QuantitySelector';
import { useTranslation } from 'react-i18next';
import { formatCurrency } from '../../../utils/format';

type Props = {
  item: CartItem;
  onQuantityChange: (slug: string, quantity: number) => void;
};

const OrderItemRow: React.FC<Props> = ({ item, onQuantityChange }) => {
  const { i18n } = useTranslation();
  return (
    <tr className="order-summary-item">
      <td>
        <img src={item.image} alt={item.product} className="product-image" />
      </td>
      <td>
        {item.title}<br />
        <small>{item.subTitle}</small>
      </td>
      <td>
        <QuantitySelector
          value={item.quantity}
          onChange={(newQuantity) => onQuantityChange(item.slug, newQuantity)}
        />
      </td>
      <td className="order-summary-price">{formatCurrency(item.price, i18n.language)}</td>
    </tr>
  );
};

export default OrderItemRow;
