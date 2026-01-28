import { Category } from "../services/category/categoriesService";
import { ProductImage } from "./ProductImage";

export type ProductPreviewType = {
  name: string;
  price: number;
  slug: string;
  categories: Category[];
  images: ProductImage[];
  onClick?: () => void;
  active: boolean;
};
