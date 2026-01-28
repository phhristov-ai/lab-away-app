import { mapProductImages } from "./productImage.mapper";

export const transformProducts = (backendProducts: any[]) =>
  backendProducts.map(product => ({
    name: product.name,
    price: product.price,
    slug: product.slug,
    categories: product.categories ?? [],
    images: mapProductImages(product),
    active: product.active,
  }));