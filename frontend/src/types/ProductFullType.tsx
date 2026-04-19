import { Media } from "../components/common/layout/ImageTextSection";
import { Category } from "../services/category/categoriesService";

export type Faq = {
  question: string;
  answer: string;
};

export type ProductFullType = {
  name: string;
  slug: string;
  price: number;
  stock: number;
  description: string;
  categories : Category[];
  createdAt: string;
  updatedAt: string;
  images: ProductImage[];
  faqs: Faq[];
  banner?: Media;
};