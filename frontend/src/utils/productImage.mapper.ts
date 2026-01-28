import { ProductImage } from "../types/ProductImage";

const EMPTY_IMAGE: ProductImage = {
  imageUrlSmall: "/images/placeholder-small.png",
  imageUrlMedium: "/images/placeholder-medium.png",
  imageUrlLarge: "/images/placeholder-large.png",
  main: true,
};

export const mapProductImages = (product: any): ProductImage[] => {
  let images: ProductImage[] = [];

  // New BE format
  if (Array.isArray(product.images) && product.images.length > 0) {
    images = product.images.map((img: any) => ({
      imageUrlSmall: img.imageUrlSmall ?? "",
      imageUrlMedium: img.imageUrlMedium ?? "",
      imageUrlLarge: img.imageUrlLarge ?? "",
      main: Boolean(img.main),
    }));
  }
  // Legacy BE format
  else if (product.imageUrls) {
    const { small = "", medium = "", large = "" } = product.imageUrls;

    images = [
      {
        imageUrlSmall: small,
        imageUrlMedium: medium,
        imageUrlLarge: large,
        main: true,
      },
    ];
  }

  return images.length ? images : [EMPTY_IMAGE];
};