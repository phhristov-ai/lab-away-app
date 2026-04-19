import { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { ProductFullType } from '../../types/ProductFullType';
import { ProductPreviewType } from '../../types/ProductPreviewType';
import { createProduct, deleteProduct, fetchProductBySlug, ProductImageDto, ProductPayloadDto, updateProduct } from '../../services/product/productService';
import { useAdmin } from '../../context/AdminContext';
import { getColumns, getFaqItems, getFeatureItems } from '../../services/product/productPageContent';
import { t } from 'i18next';
import { Category, fetchCategories } from '../../services/category/categoriesService';
import { trackViewItem } from '../../utils/analytics';
import { Media } from '../../components/common/layout/ImageTextSection';
import ScientistImageLarge from '../../assets/images/product/Scientist_768.webp';
import { mapBannerFromApi } from '../../utils/productImage.mapper';

export const useProductPage = () => {
    const { slug } = useParams<{ slug: string }>();
    const location = useLocation();
    const { i18n } = useTranslation();
    const { isAdmin } = useAdmin();
    const navigate = useNavigate();
    const isFullProduct = (product: any): product is ProductFullType => {
        return 'images' in product && Array.isArray(product.images);
    };
    const [product, setProduct] = useState<ProductFullType | ProductPreviewType | null>(
        location.state?.product ?? {}
    );

    const getInitialImages = (
        product: ProductFullType | ProductPreviewType | null
    ): ProductImage[] => {
        if (!product) return [];
        return product.images ?? [];
    };

    const initialImages = getInitialImages(product);
    const [images, setImages] = useState<ProductImage[]>(initialImages);

    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const faqItems = getFaqItems(t);
    const columns = getColumns(t);
    const featureItems = getFeatureItems(t);

    const [allCategories, setAllCategories] = useState<Category[]>([]);
    const [selectedCategories, setSelectedCategories] = useState<Category[]>([]);
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [active, setActive] = useState<boolean | undefined>(undefined);
    const [price, setPrice] = useState(0);
    const [bannerFile, setBannerFile] = useState<File | null>(null);
    const [showConfirmDelete, setShowConfirmDelete] = useState(false);
    const handleDeleteClick = () => {
        setShowConfirmDelete(true);
    };
    const fallbackMedia: Media = {
        type: 'image',
        src: ScientistImageLarge
    };

    useEffect(() => {
        if (product && isFullProduct(product)) {
            trackViewItem({
                item_id: product.slug,
                item_name: product.name,
                price: product.price,
                quantity: 1,
                item_category: product?.categories[0]?.name,
                item_category2: product?.categories[1]?.name,
            });
        }
    }, [product]);

    useEffect(() => {
        fetchCategories()
            .then(setAllCategories)
            .catch(err => console.error('Failed to fetch categories', err));
    }, [i18n.language]);

    useEffect(() => {
        if (
            isFullProduct(product) &&
            Array.isArray(product.categories) &&
            product.categories.length > 0 &&
            allCategories.length > 0
        ) {
            const productCategorySlugs = new Set(
                product.categories.map(cat => cat.slug)
            );

            const selected = allCategories.filter(cat =>
                productCategorySlugs.has(cat.slug)
            );

            setSelectedCategories(selected);
        }
    }, [product, allCategories]);

    const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const selectedOptions = Array.from(e.target.selectedOptions);
        const selected = selectedOptions.map(opt => {
            const cat = allCategories.find(c => c.slug === opt.value);
            return cat!;
        });
        setSelectedCategories(selected);
    };

    const handleConfirmDelete = async () => {
        try {
            await handleDelete();
        } catch {
        } finally {
            setShowConfirmDelete(false);
        }
    };

    const handleDelete = async () => {
        if (!product?.slug) return;

        try {
            await deleteProduct(product.slug);
            navigate('/shop');
        } catch (err) {
            console.error('Error deleting post:', err);
            throw err;
        }
    };

    useEffect(() => {
        const loadProduct = async () => {
            if (!slug || slug === 'new') return;

            try {
                const fullProduct = await fetchProductBySlug(slug);

                setProduct(fullProduct);
                setTitle(fullProduct.name);
                setPrice(fullProduct.price);
                setDescription(fullProduct.description);
                setActive(fullProduct.active);
                setProduct({
                    ...fullProduct,
                    banner: mapBannerFromApi(fullProduct.banner)
                });
                setImages(fullProduct.images ?? []);

            } catch (error) {
                console.error('Error fetching product by slug:', error);
            }
        };

        loadProduct();
    }, [slug, i18n.language]);

    const handleSave = async () => {
        try {
            const isNew = slug === 'new';
            const language = i18n.language.toUpperCase();

            if (!isNew && !isFullProduct(product)) {
                console.warn('Product is not fully loaded');
                return;
            }

            const {
                filesToUpload,
                existingImages,
            } = processImagesForPayload(images);

            const imagesPayload: ProductImageDto[] = existingImages.map(img => ({
                imageUrlSmall: img.imageUrlSmall,
                imageUrlMedium: img.imageUrlMedium,
                imageUrlLarge: img.imageUrlLarge,
            }));

            const payload: ProductPayloadDto = {
                price,
                stock: 10,
                active: active,
                categories: selectedCategories.map(cat => cat.slug),
                translation: {
                    language,
                    name: title,
                    description,
                },
                images: imagesPayload
            };

            const result = isNew
                ? await createProduct(payload, filesToUpload, bannerFile ?? undefined)
                : await updateProduct(slug!, payload, filesToUpload, bannerFile ?? undefined);

            setSuccessMessage(isNew ? 'Product created successfully!' : 'Product updated successfully!');
            setShowSuccessModal(true);

            setTimeout(() => {
                navigate(`/product/${result.slug}`);
            }, 1500);
        } catch (err) {
            console.error('Error saving product:', err);
        }
    };

    type NormalizedImageData = {
        filesToUpload: File[];
        existingImages: ProductImage[];
    };

    function processImagesForPayload(images: ProductImage[]): NormalizedImageData {
        const filesToUpload = images
            .filter(img => img.file)
            .map(img => img.file!);

        const existingImages = images.filter(img => !img.file);

        return {
            filesToUpload,
            existingImages,
        };
    }
    const updateBannerFromFile = (file: File) => {
        setBannerFile(file);
        setBannerPreview(file);
    };

    const setBannerPreview = (file: File) => {
        const media = createMediaFromFile(file);
        setProduct(prev => {
            if (!prev || !isFullProduct(prev)) return prev;

            return {
                ...prev,
                banner: media
            };
        });
    };

    const createMediaFromFile = (file: File): Media => {
        const url = URL.createObjectURL(file);

        if (file.type.startsWith('video')) {
            return {
                type: 'video',
                src: url,
                mime: file.type
            };
        }

        return {
            type: 'image',
            src: url
        };
    };

    const getBannerMedia = (
        product: ProductFullType | ProductPreviewType | null | undefined
    ): Media => {
        if (product && 'banner' in product && product.banner) {
            return product.banner;
        }

        return fallbackMedia;
    };

    return {
        slug,
        product,
        setProduct,
        title,
        setTitle,
        description,
        setDescription,
        price,
        setPrice,
        isFullProduct,
        isAdmin,
        handleSave,
        showConfirmDelete,
        handleDeleteClick,
        handleConfirmDelete,
        setShowConfirmDelete,
        showSuccessModal,
        setShowSuccessModal,
        successMessage,
        faqItems,
        columns,
        featureItems,
        i18n,
        allCategories,
        selectedCategories,
        handleCategoryChange,
        images,
        setImages,
        active,
        setActive,
        updateBannerFromFile,
        getBannerMedia
    };
};
