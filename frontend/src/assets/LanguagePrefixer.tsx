import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import i18n from '../i18n/i18n';

const supportedLanguages = new Set(['en', 'de', 'bg']);

const LanguagePrefixer = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const path = location.pathname;

  const pathLangRaw = path.split('/')[1] || '';
  const pathLang = pathLangRaw.split('-')[0];

  const hasSupportedLang = supportedLanguages.has(pathLang);

  const rawLang = i18n.language || 'en';
  const currentLang = rawLang.split('-')[0];

  useEffect(() => {
    if (!hasSupportedLang) {
      const target = `/${currentLang}${path.startsWith('/') ? '' : '/'}${path.slice(1)}`;

      if (location.pathname !== target) {
        navigate(target, {
          replace: true,
          state: location.state,
        });
      }
    } else if (pathLang !== pathLangRaw) {
      const restOfPath = path.split('/').slice(2).join('/');
      const normalizedPath = `/${pathLang}/${restOfPath}`;

      if (location.pathname !== normalizedPath) {
        navigate(normalizedPath, {
          replace: true,
          state: location.state,
        });
      }
    }
  }, [hasSupportedLang, currentLang, path, pathLang, pathLangRaw, navigate, location.pathname, location.state]);

  return null;
};

export default LanguagePrefixer;
