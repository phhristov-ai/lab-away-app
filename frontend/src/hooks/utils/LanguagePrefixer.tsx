import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import i18n from '../../i18n/i18n';

const supportedLanguages = new Set(['en', 'de', 'bg']);

const LanguagePrefixer = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const path = location.pathname;

  const pathLangRaw = path.split('/')[1] || '';
  const pathLang = pathLangRaw.split('-')[0];
  const hasSupportedLang = supportedLanguages.has(pathLang);

  const currentLang = (i18n.language || 'en').split('-')[0];

  useEffect(() => {
    if (!hasSupportedLang) {
      const pathWithoutLeadingSlash = path.startsWith('/')
        ? path.slice(1)
        : path;

      const target = `/${currentLang}/${pathWithoutLeadingSlash}`;

      if (location.pathname !== target) {
        navigate(target, {
          replace: true,
          state: location.state,
        });
      }
      return;
    }

    if (pathLang !== pathLangRaw) {
      const restOfPath = path.split('/').slice(2).join('/');
      const normalizedPath = `/${pathLang}/${restOfPath}`;

      if (location.pathname !== normalizedPath) {
        navigate(normalizedPath, {
          replace: true,
          state: location.state,
        });
      }
    }
  }, [
    hasSupportedLang,
    currentLang,
    path,
    pathLang,
    pathLangRaw,
    navigate,
    location.pathname,
    location.state,
  ]);

  return null;
};

export default LanguagePrefixer;