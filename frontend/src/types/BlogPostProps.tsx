import { Category } from "../services/categoriesService";

type BlogPostProps = {
    title: string;
    content: string;
    image: string;
    slug: string;
    category: string;
    readingTime: number;
    date: string;
    categories: Category[];
};