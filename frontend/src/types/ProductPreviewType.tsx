import { Category } from "../services/category/categoriesService";

export type ProductPreviewType = {
  name: string;
  price: number;
  slug: string;
  categories: Category[];
  images: ProductImage[];
  onClick?: () => void;
  active: boolean;
};
