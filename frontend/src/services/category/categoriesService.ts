import i18n from '../../i18n/i18n';
import { axiosInstance } from '../api/api';

export type Category = {
  name: string;
  slug: string;
};

export const fetchCategories = async (): Promise<Category[]> => {
  const response = await axiosInstance.get('/categories', {
    params: { lang: i18n.language.toUpperCase() },
  });
  return response.data;
};
