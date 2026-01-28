import { useTranslation } from 'react-i18next';
import { useCallback } from 'react';
import { useAdmin } from '../../context/AdminContext';
import { formatCurrency } from '../../utils/format';

type UseProductInfoParams = {
  title: string;
  price: number;
  onTitleChange?: (newTitle: string) => void;
  onPriceChange?: (newPrice: number) => void;
};

export const useProductInfo = ({
  title,
  price,
  onTitleChange,
  onPriceChange,
}: UseProductInfoParams) => {
  const { isAdmin } = useAdmin();
  const { i18n } = useTranslation();

  const renderTitle = useCallback(() => {
    if (isAdmin) {
      return (
        <div className="admin-field">
          <label className="admin-label">Title</label>
          <input
            type="text"
            placeholder="Enter title"
            value={title}
            onChange={(e) => onTitleChange?.(e.target.value)}
            className="admin-input"
          />
        </div>
      );
    }

    if (title) {
      return (
        <div className="product-title">
          <h1>{title}</h1>
        </div>
      );
    }

    return <div className="title-placeholder" />;
  }, [isAdmin, title, onTitleChange]);

  const renderPrice = useCallback(() => {
    if (isAdmin) {
      return (
        <div className="admin-field">
          <label className="admin-label">Price</label>
          <input
            type="number"
            placeholder="Enter price"
            value={price}
            onChange={(e) => {
              const value = Number.parseFloat(e.target.value);
              if (!Number.isNaN(value)) onPriceChange?.(value);
            }}
            className="admin-input"
          />
        </div>
      );
    }

    if (price > 0) {
      return (
        <div className="product-price">
          <span>{formatCurrency(price, i18n.language)}</span>
        </div>
      );
    }

    return <div className="price-placeholder" />;
  }, [isAdmin, price, i18n.language, onPriceChange]);

  return {
    renderTitle,
    renderPrice,
  };
};
