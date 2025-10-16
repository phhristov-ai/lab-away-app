import { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import i18n from '../i18n/i18n';

const supportedLanguages = ['en', 'de', 'bg'];

const LanguagePrefixer = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const path = location.pathname;

  const pathLang = path.split('/')[1];
  const hasLang = supportedLanguages.includes(pathLang);

  const currentLang = i18n.language || 'en';

  useEffect(() => {
    if (!hasLang) {
      const target = `/${currentLang}${path}`;

      if (location.pathname !== target) {
        navigate(target, {
          replace: true,
          state: location.state,
        });
      }
    }
  }, [hasLang, currentLang, path, navigate, location.pathname, location.state]);

  return null;
};

export default LanguagePrefixer;
