import React from 'react';
import './ShopHeader.css';
import { useTranslation } from 'react-i18next';

const ShopHeader: React.FC = () => {
  const { t } = useTranslation();

  return (
    <div className="shop-header">
      <h1 className="shop-title">{t('shop.title')}</h1>
      <p className="shop-subtitle">
        {t('shop.subtitle')}
      </p>
    </div>
  );
};

export default ShopHeader;
