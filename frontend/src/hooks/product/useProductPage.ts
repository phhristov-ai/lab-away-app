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
import { ProductImage } from '../../types/ProductImage';
import { trackViewItem } from '../../utils/analytics';

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
    const [enabled, setEnabled] = useState(true);
    const [price, setPrice] = useState(0);
    const [showConfirmDelete, setShowConfirmDelete] = useState(false);
    const handleDeleteClick = () => {
        setShowConfirmDelete(true);
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

    // Fetch categories
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
            // Extract slugs from product.categories
            const productCategorySlugs = product.categories.map(cat => cat.slug);

            const selected = allCategories.filter(cat =>
                productCategorySlugs.includes(cat.slug)
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
                setEnabled(fullProduct.enabled);

                if (fullProduct.images) {
                    setImages(fullProduct.images);
                }

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
                mainImageIndex,
                filesToUpload,
                existingImages,
            } = processImagesForPayload(images);

            const imagesPayload: ProductImageDto[] = existingImages.map(img => ({
                imageUrlSmall: img.imageUrlSmall,
                imageUrlMedium: img.imageUrlMedium,
                imageUrlLarge: img.imageUrlLarge,
                main: img.main,
            }));


            const payload: ProductPayloadDto = {
                price,
                stock: 10,
                active: true,
                mainImageIndex,
                categories: selectedCategories.map(cat => cat.slug),
                translation: {
                    language,
                    name: title,
                    description,
                },
                images: imagesPayload,
                enabled: enabled
            };

            const result = isNew
                ? await createProduct(payload, filesToUpload)
                : await updateProduct(slug!, payload, filesToUpload);

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
        mainImageIndex: number;
        filesToUpload: File[];
        existingImages: ProductImage[];
    };

    function processImagesForPayload(images: ProductImage[]): NormalizedImageData {
        const normalizedImages = normalizeMainImage(images);

        const mainImageIndex = normalizedImages.findIndex(img => img.main);

        const filesToUpload = normalizedImages
            .filter(img => img.file)
            .map(img => img.file!);

        const existingImages = normalizedImages.filter(img => !img.file);

        return {
            mainImageIndex,
            filesToUpload,
            existingImages,
        };
    }


    function normalizeMainImage(images: ProductImage[]): ProductImage[] {
        if (images.length === 0) return [];
        const mainIndex = images.findIndex(img => img.main);

        const validMainIndex = Math.max(mainIndex, 0);

        return images.map((img, idx) => ({
            ...img,
            main: idx === validMainIndex,
        }));
    }

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
        enabled,
        setEnabled
    };
};
