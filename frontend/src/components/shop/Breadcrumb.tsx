// Breadcrumb.tsx
import React from 'react';
import { Link } from 'react-router-dom';
import './Breadcrumb.css';
import { useTranslation } from 'react-i18next';

const Breadcrumb: React.FC = () => {
  const { t } = useTranslation();
  return (
    <div className="breadcrumb">
      <Link to="/">{t('shop.breadcrumb.home')}</Link> / <span>{t('shop.breadcrumb.certifiedTests')}</span>
    </div>
  );
};

export default Breadcrumb;
