import { Media } from "../components/common/layout/ImageTextSection";

export const mapProductImages = (product: any): ProductImage[] => {
  let images: ProductImage[] = [];

  const makeStableId = (img: any, index: number) =>
    img.id ??
    img.imageUrlSmall ??
    `legacy-${index}`; // ✅ stable, deterministic

  if (Array.isArray(product.images) && product.images.length > 0) {
    images = product.images.map((img: any, index: number) => ({
      id: makeStableId(img, index), // ✅ NEVER random
      imageUrlSmall: img.imageUrlSmall ?? "",
      imageUrlMedium: img.imageUrlMedium ?? "",
      imageUrlLarge: img.imageUrlLarge ?? "",
      order: img.order ?? index,
    }));
  } else if (product.imageUrls) {
    const { small = "", medium = "", large = "" } = product.imageUrls;

    images = [
      {
        id: small || "single-image", // ✅ stable fallback
        imageUrlSmall: small,
        imageUrlMedium: medium,
        imageUrlLarge: large,
        order: 0,
      },
    ];
  }

  return images.length
    ? images
    : [
        {
          id: "placeholder",
          imageUrlSmall: "/images/placeholder-small.png",
          imageUrlMedium: "/images/placeholder-medium.png",
          imageUrlLarge: "/images/placeholder-large.png",
          order: 0,
        },
      ];
};

export const mapBannerFromApi = (banner: any): Media | undefined => {
  if (!banner) return undefined;

  const type = banner.type?.toLowerCase();

  if (type === 'video') {
    return {
      type: 'video',
      src: banner.url,
    };
  }

  return {
    type: 'image',
    src: banner.url,
  };
};