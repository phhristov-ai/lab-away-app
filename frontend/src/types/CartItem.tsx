import { Category } from "../services/categoriesService";

export type CartItem = {
  name: string;
  price: number;
  quantity: number;
  title: string;
  product: string;
  subtotal: number;
  inclVat?: string; 
  image: string;
  subTitle?: string;
  slug: string;
  categories: Category[];
};
