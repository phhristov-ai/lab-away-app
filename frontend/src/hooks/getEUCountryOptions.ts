import countries from 'i18n-iso-countries';
import en from 'i18n-iso-countries/langs/en.json';
import de from 'i18n-iso-countries/langs/de.json';
import bg from 'i18n-iso-countries/langs/bg.json';

countries.registerLocale(en);
countries.registerLocale(de);
countries.registerLocale(bg);


const EU_COUNTRY_CODES = [
  'AT', 'BE', 'BG', 'HR', 'CY', 'CZ', 'DK', 'EE', 'FI', 'FR',
  'DE', 'GR', 'HU', 'IE', 'IT', 'LV', 'LT', 'LU', 'MT', 'NL',
  'PL', 'PT', 'RO', 'SK', 'SI', 'ES', 'SE'
];

export const getEUCountryOptions = (language: string): { code: string; name: string }[] => {
  return EU_COUNTRY_CODES.map((code) => ({
    code,
    name: countries.getName(code, language) || code,
  }));
};
