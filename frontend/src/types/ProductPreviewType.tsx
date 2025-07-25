import { Category } from "../services/categoriesService";

export type ProductPreviewType = {
  name: string;
  price: number;
  thumbnailUrl: string;
  slug: string;
  categories: Category[];
};
