// LanguageLayout.tsx

import { Outlet, Navigate } from 'react-router-dom';
import { useEffect } from 'react';
import i18n from '../i18n/i18n';

interface LanguageLayoutProps {
  lang: string;
}

const supportedLanguages = new Set(['en', 'de', 'bg']);

const LanguageLayout = ({ lang }: LanguageLayoutProps) => {
  useEffect(() => {
    if (supportedLanguages.has(lang)) {
      i18n.changeLanguage(lang);
    }
  }, [lang]);

  if (!supportedLanguages.has(lang)) {
    return <Navigate to="/en" replace />;
  }

  return <Outlet />;
};

export default LanguageLayout;
