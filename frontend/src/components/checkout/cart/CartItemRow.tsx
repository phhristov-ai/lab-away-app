import React from 'react';
import QuantitySelector from "../../product/QuantitySelector";
import './CartItems.css';
import { useCart } from "../../../context/CartContext";
import './CartItemRow.css';
import { useTranslation } from 'react-i18next';
import { formatCurrency } from '../../../utils/format';
import { Link } from 'react-router-dom';
import { Category } from '../../../services/category/categoriesService';
import { CartItem } from '../../../types/CartItem';

type Props = {
  item: CartItem & {
    image?: string;
    product?: string;
    title?: string;
    subtotal?: number;
    inclVat?: string;
    slug: string;
    categories: Category[];
  };
};

const CartItemRow: React.FC<Props> = ({ item }) => {
  const { dispatch } = useCart();
  const { t, i18n } = useTranslation();

  const handleQuantityChange = (quantity: number) => {
    dispatch({ type: 'UPDATE_QUANTITY', payload: { slug: item.slug, quantity: quantity, categories: item.categories } });
  };

  const handleRemoveItem = () => {
    dispatch({ type: 'REMOVE_ITEM', payload: { slug: item.slug, categories: item.categories } });
  };

  return (
    <tr>
      <td data-label="Product">
        <span className="cell-label">{t("checkout.cart.columns.product")}</span>
        <div className="cell-value">
          <div className="product-cell">
            <button className="remove-button" onClick={handleRemoveItem}>×</button>
            <Link to={`/product/${item.slug}`}>
              <img src={item.image} alt={item.product} className="product-image" />
            </Link>
            <div className="product-info">
              <Link to={`/product/${item.slug}`} className="product-title-link">
                <span className="product-title">{item.title}</span>
              </Link>
            </div>
          </div>
        </div>
      </td>

      <td data-label="Price">
        <span className="cell-label">{t("checkout.cart.columns.price")}</span>
        <div className="cell-value">
          {formatCurrency(item.price, i18n.language)}
        </div>
      </td>

      <td data-label="Quantity">
        <span className="cell-label">{t("checkout.cart.columns.quantity")}</span>
        <div className="cell-value">
          <QuantitySelector
            value={item.quantity}
            onChange={handleQuantityChange}
          />
        </div>
      </td>

      <td data-label="Subtotal">
        <span className="cell-label">{t("checkout.cart.columns.subtotal")}</span>
        <div className="cell-value">
          <div className="subtotal-wrapper">
            {formatCurrency(item.subtotal, i18n.language)}
            <span className="vat-text">
              <sub>{item.inclVat}</sub>
            </span>
          </div>
        </div>
      </td>

    </tr>
  );

};

export default CartItemRow;
