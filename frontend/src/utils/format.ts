
export const formatCurrency = (
  value: number | string,
  locale: string,
  currency: string = 'EUR'
): string => {
  const number = typeof value === 'string' ? Number.parseFloat(value) : value;

  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
    minimumFractionDigits: number % 1 === 0 ? 0 : 2,
    maximumFractionDigits: 2,
  }).format(number);
};

export const formatDate = (dateString: string, locale: string = 'en-US') => {
  const date = new Date(dateString);
  return date.toLocaleDateString(locale, {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  });
};
