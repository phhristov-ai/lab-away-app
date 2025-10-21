import React from 'react';
import QuantitySelector from '../../product/QuantitySelector';
import { Link } from 'react-router-dom';
import { useCart } from "../../../context/CartContext";

type Props = {
  item: CartItem;
  onQuantityChange: (slug: string, quantity: number) => void;
};

const OrderItemRow: React.FC<Props> = ({ item, onQuantityChange }) => {
  const { dispatch } = useCart();

  const handleRemoveItem = () => {
    dispatch({ type: 'REMOVE_ITEM', payload: { slug: item.slug } });
  };

  return (
    <tr className="order-summary-item">
      <td colSpan={2}>
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
      <td className="remove-button-container">
        <button className="remove-button" onClick={handleRemoveItem}>×</button>
      </td>
    </tr>
  );
};

export default OrderItemRow;
