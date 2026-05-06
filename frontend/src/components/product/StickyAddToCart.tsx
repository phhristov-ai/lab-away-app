import React, { useEffect, useState } from 'react';
import { Category } from '../../services/category/categoriesService';
import AddToCartButton from './AddToCartButton';
import './StickyAddToCart.css';
import { useTranslation } from 'react-i18next';

type Props = {
    title: string;
    price: number;
    slug: string;
    image: string;
    quantity: number;
    categories: Category[];
    active: boolean | undefined;
    onAddToCart: () => void;
};

const StickyAddToCart: React.FC<Props> = ({
    title,
    price,
    slug,
    image,
    quantity,
    categories,
    active,
    onAddToCart,
}) => {
    const [visible, setVisible] = useState(false);
    const { t } = useTranslation();
    useEffect(() => {
        const handleScroll = () => {
            setVisible(window.scrollY > 300);
        };

        window.addEventListener('scroll', handleScroll);
        return () => window.removeEventListener('scroll', handleScroll);
    }, []);

    if (!visible) return null;

    return (
        <div className="sticky-add-to-cart">
            <div className="sticky-content">
                <div className="sticky-left">
                    <img
                        src={image}
                        alt={title}
                        className="sticky-image"
                    />

                    <div className="sticky-info">
                        <span className="sticky-title">{title}</span>
                        <span className="sticky-price">{price} €</span>
                    </div>
                </div>

                <AddToCartButton
                    onClick={onAddToCart}
                    label={t('productPage.buttons.addToCart')}
                    slug={slug}
                    name={title}
                    image={image}
                    price={price}
                    quantity={quantity}
                    categories={categories}
                    active={active}
                    variant="primary"
                />
            </div>
        </div>
    );
};

export default StickyAddToCart;