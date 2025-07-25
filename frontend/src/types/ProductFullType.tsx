import { Category } from "../services/categoriesService";
import { ProductImage } from "./ProductImage";

export type SharedContent = {
  key: string;
  title: string;
  content: string;
  imageUrl: string;
};

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
  sharedContent: SharedContent[];
  faqs: Faq[];
};