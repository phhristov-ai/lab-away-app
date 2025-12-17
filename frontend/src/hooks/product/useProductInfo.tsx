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
        <input
          type="text"
          placeholder="Enter title"
          value={title}
          onChange={(e) => onTitleChange?.(e.target.value)}
          className="post-input"
        />
      );
    }
    if (title) {
      return <h1>{title}</h1>;
    }
    return <div className="title-placeholder" />;
  }, [isAdmin, title, onTitleChange]);

  const renderPrice = useCallback(() => {
    if (isAdmin) {
      return (
        <input
          type="number"
          placeholder="Enter price"
          value={price}
          onChange={(e) => {
            const value = parseFloat(e.target.value);
            if (!isNaN(value)) onPriceChange?.(value);
          }}
          className="post-input"
        />
      );
    }

    if (price > 0) {
      return <p className="price-text">{formatCurrency(price, i18n.language)}</p>;
    }

    return <div className="price-placeholder" />;
  }, [isAdmin, price, i18n.language, onPriceChange]);

  return {
    renderTitle,
    renderPrice,
  };
};
