import { Category } from "../services/categoriesService";

type BlogPostProps = {
  title: string;
  content: string;
  imageUrls: {
    small: string;
    medium: string;
    large: string;
  };
  slug: string;
  categories: Category[];
  readingTime: number;
  date: string;
};
