import React from 'react';
import QuantitySelector from '../../product/QuantitySelector';
import { Link } from 'react-router-dom';

type Props = {
  item: CartItem;
  onQuantityChange: (slug: string, quantity: number) => void;
};

const OrderItemRow: React.FC<Props> = ({ item, onQuantityChange }) => {
  return (
    <tr className="order-summary-item">
      <td>
        <Link to={`/product/${item.slug}`}>
          <img src={item.image} alt={item.product} className="product-image" />
        </Link>
      </td>
      <td colSpan={2}>
        <Link to={`/product/${item.slug}`} className="product-title-link">
          {item.title}
        </Link>
        <br />
        <small>
          <img
            src="/static/media/clock.73a198ac8c0a3163c5ed.webp"
            alt=""
            className="feature-icon"
          />
          {item.subTitle}
        </small>
      </td>
      <td>
        <QuantitySelector
          value={item.quantity}
          onChange={(newQuantity) => onQuantityChange(item.slug, newQuantity)}
        />
      </td>
    </tr>
  );
};

export default OrderItemRow;
