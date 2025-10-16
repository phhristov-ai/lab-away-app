// LanguageLayout.tsx

import { Outlet, Navigate } from 'react-router-dom';
import { useEffect } from 'react';
import i18n from '../i18n/i18n';

interface LanguageLayoutProps {
  lang: string;
}

const supportedLanguages = ['en', 'de', 'bg'];

const LanguageLayout = ({ lang }: LanguageLayoutProps) => {
  // Optionally check supportedLanguages.includes(lang)
  useEffect(() => {
    if (supportedLanguages.includes(lang)) {
      i18n.changeLanguage(lang);
    } else {
      // maybe fallback or redirect
    }
  }, [lang]);

  if (!supportedLanguages.includes(lang)) {
    return <Navigate to="/en" replace />;
  }

  return <Outlet />;
};

export default LanguageLayout;
