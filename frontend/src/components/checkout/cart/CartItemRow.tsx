import React from 'react';
import QuantitySelector from "../../product/QuantitySelector";
import './CartItems.css';
import { useCart } from "../../../context/CartContext";
import './CartItemRow.css';
import { useTranslation } from 'react-i18next';
import { formatCurrency } from '../../../utils/format';
import { Link } from 'react-router-dom';

type Props = {
  item: CartItem & {
    image?: string;
    product?: string;
    title?: string;
    subtotal?: number;
    inclVat?: string;
    slug: string;
  };
};

const CartItemRow: React.FC<Props> = ({ item }) => {
  const { dispatch } = useCart();
  const { i18n } = useTranslation();

  const handleQuantityChange = (quantity: number) => {
    dispatch({ type: 'UPDATE_QUANTITY', payload: { slug: item.slug, quantity } });
  };

  const handleRemoveItem = () => {
    dispatch({ type: 'REMOVE_ITEM', payload: { slug: item.slug } });
  };

  return (
    <tr>
      <td data-label="Product">
        <div className="product-cell">
          <button className="remove-button" onClick={handleRemoveItem}>×</button>
          <Link to={`/product/${item.slug}`}>
            <img
              src={item.image}
              alt={item.product}
              className="product-image"
            />
          </Link>
          <div className="product-info">
            <Link to={`/product/${item.slug}`} className="product-title-link">
              <span className="product-title" >
                {item.title}
              </span>
            </Link>
          </div>
        </div>
      </td>

      <td data-label="Price">{formatCurrency(item.price, i18n.language)}</td>

      <td data-label="Quantity">
        <QuantitySelector
          value={item.quantity}
          onChange={handleQuantityChange}
        />
      </td>

      <td data-label="Subtotal">
        <div className="subtotal-wrapper">
          {formatCurrency(item.subtotal, i18n.language)}
          <span className="vat-text"><sub>{item.inclVat}</sub></span>
        </div>
      </td>
    </tr>
  );

};

export default CartItemRow;
