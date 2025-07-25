import { useTranslation } from 'react-i18next';

import flagUS from '../../assets/icons/European-Union.Flag.svg';
import flagDE from '../../assets/icons/Germany.svg';
import flagBG from '../../assets/icons/Bulgaria.Flag.svg';

const LanguageDropdown = () => {
  const { i18n } = useTranslation();

  const languages = [
    { code: 'en', flag: flagUS, alt: 'English' },
    { code: 'de', flag: flagDE, alt: 'German' },
    { code: 'bg', flag: flagBG, alt: 'Bulgarian' },
  ];

  const changeLanguage = (lng: string) => {
    i18n.changeLanguage(lng);
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
          <li key={code} onClick={() => changeLanguage(code)}>
            <img className="language-icon" src={flag} alt={code} />
          </li>
        ))}
      </ul>
    </div>
  );
};

export default LanguageDropdown;
