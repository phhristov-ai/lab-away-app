import React from 'react';
import QuantitySelector from '../../product/QuantitySelector';
import { Link } from 'react-router-dom';
import { useCart } from "../../../context/CartContext";
import { CartItem } from '../../../types/CartItem';
import { Category } from '../../../services/category/categoriesService';

type Props = {
  item: CartItem;
  onQuantityChange: (slug: string, quantity: number, categories: Category[]) => void;
};

const OrderItemRow: React.FC<Props> = ({ item, onQuantityChange }) => {
  const { dispatch } = useCart();

  const handleRemoveItem = () => {
    dispatch({ type: 'REMOVE_ITEM', payload: { slug: item.slug, categories: item.categories } });
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
      </td>
      <td>
        <QuantitySelector
          value={item.quantity}
          onChange={(newQuantity) => onQuantityChange(item.slug, newQuantity, item.categories)}
        />
      </td>
      <td className="remove-button-container">
        <button className="remove-button" onClick={handleRemoveItem}>×</button>
      </td>
    </tr>
  );
};

export default OrderItemRow;
