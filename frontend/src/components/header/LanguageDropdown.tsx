import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';

import flagUS from '../../assets/icons/European-Union.Flag.svg';
import flagDE from '../../assets/icons/Germany.svg';

const LanguageDropdown = () => {
  const { i18n } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();

  const languages = [
    { code: 'en', flag: flagUS, alt: 'English' },
    { code: 'de', flag: flagDE, alt: 'German' }
  ];

  const changeLanguage = (newLang: string) => {
    const currentPath = location.pathname;
    const parts = currentPath.split('/');
    const currentLang = parts[1];

    if (['en', 'de'].includes(currentLang)) {
      parts[1] = newLang;
    } else {
      parts.unshift(newLang);
    }

    const newPath = parts.join('/') || '/';

    i18n.changeLanguage(newLang);
    navigate(newPath);
  };

  const availableLanguages = languages.filter(lang => lang.code !== i18n.language);

  return (
    <div className="language-dropdown">
      <button className="dropdown-btn">
        <img
          className="language-icon"
          src={languages.find(lang => lang.code === i18n.language)?.flag}
          alt={languages.find(lang => lang.code === i18n.language)?.alt}
        />
      </button>
      <ul className="dropdown-menu">
        {availableLanguages.map(({ code, flag }) => (
          <li key={code}>
            <button
              type="button"
              className="language-option"
              onClick={() => changeLanguage(code)}
              aria-label={`Change language to ${code.toUpperCase()}`}
            >
              <img className="language-icon" src={flag} alt={code} />
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default LanguageDropdown;
