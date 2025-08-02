import { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { ProductFullType } from '../types/ProductFullType';
import { ProductPreviewType } from '../types/ProductPreviewType';
import { createProduct, deleteProduct, fetchProductBySlug, ProductPayloadDto, updateProduct } from '../services/productService';
import { useAdmin } from '../context/AdminContext';
import { getColumns, getFaqItems, getFeatureItems } from '../services/productPageContent';
import { t } from 'i18next';
import { Category, fetchCategories } from '../services/categoriesService';
import { ProductImage } from '../types/ProductImage';
import { BreadcrumbItem } from '../components/shop/Breadcrumb';

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
    const getInitialImages = (product: ProductFullType | ProductPreviewType | null): ProductImage[] => {
        if (!product) return [];
        if (isFullProduct(product)) return product.images;
        return [{ imageUrl: product.thumbnailUrl, main: true }];
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
    const [price, setPrice] = useState(0);
    const [showConfirmDelete, setShowConfirmDelete] = useState(false);
    const handleDeleteClick = () => {
        setShowConfirmDelete(true);
    };

    const imageFiles: File[] = images
        .map(img => img.file)
        .filter((file): file is File => file !== undefined);

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

    useEffect(() => {
        setImages(getInitialImages(product));
    }, [product]);


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
                setProduct((prev: any) => ({ ...prev, ...fullProduct }));
                setTitle(fullProduct.name);
                setPrice(fullProduct.price);
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

            const filesToUpload = images
                .filter(img => img.file)
                .map(img => img.file as File);

            const mainImageIndex = images.findIndex(img => img.main);

            const payload: ProductPayloadDto = {
                price,
                stock: 10, //TODO to fix later
                active: true,
                mainImageIndex,
                categories: selectedCategories.map(cat => cat.slug),
                translation: {
                    language,
                    name: title,
                    description: 'Description', //TODO to fix later
                },
            };

            let result;
            if (isNew) {
                result = await createProduct(payload, filesToUpload);
            } else {
                result = await updateProduct(slug!, payload, filesToUpload);
            }

            const updatedSlug = result.slug;

            setSuccessMessage(isNew ? 'Product created successfully!' : 'Product updated successfully!');
            setShowSuccessModal(true);

            setTimeout(() => {
                navigate(`/product/${updatedSlug}`);
            }, 1500);
        } catch (err) {
            console.error('Error saving product:', err);
        }
    };

    const breadcrumbItems: BreadcrumbItem[] = [
        { label: t('shop.breadcrumb.home'), to: '/' },
    ];

    if (product?.categories?.length) {
        breadcrumbItems.push({
            label: product.categories[0].name,
            to: `/shop?category=${product.categories[0].slug}`,
        });
    }

    if (product?.name) {
        breadcrumbItems.push({ label: product.name });
    }

    const hasFullBreadcrumb = !!product?.categories?.[0] && !!product?.name;

    return {
        slug,
        product,
        setProduct,
        title,
        setTitle,
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
        breadcrumbItems,
        hasFullBreadcrumb
    };
};