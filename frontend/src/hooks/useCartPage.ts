import { useTranslation } from "react-i18next";
import { useCart } from "../context/CartContext";
import { useEffect } from "react";
import { trackGAEvent } from "../utils/analytics";

export const useCartPage = () => {
    const { state } = useCart();
    const { t } = useTranslation();

    const enrichedItems = state.items.map(item => ({
        ...item,
        inclVat: t('checkout.cart.inclVat'),
        subtotal: item.price * item.quantity,
        title: item.name,
        product: item.name,
    }));

    const subtotalValue = state.items.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const vat = 0; // You might want to calculate this dynamically later
    const shippingCost = 0; // Free shipping
    const total = subtotalValue + shippingCost;

    useEffect(() => {
        if (enrichedItems.length > 0) {
            trackGAEvent('view_cart', {
                currency: 'EUR', // or your currency
                value: total,
                items: enrichedItems.map(item => ({
                    item_id: item.slug,
                    item_name: item.name,
                    price: item.price,
                    quantity: item.quantity
                })),
            });
        }
    }, [enrichedItems, total]);

    const labels: CartLabels = {
        cartTotalsTitle: t('checkout.cartTotals.title'),
        description: t('checkout.cartTotals.description'),
        amount: t('checkout.cartTotals.amount'),
        subtotal: t('checkout.cartTotals.subtotal'),
        shipping: t('checkout.summary.shipping'),
        total: t('checkout.summary.total'),
        vatNotePrefix: t('checkout.cartTotals.vatNotePrefix'),
        vatNoteSuffix: t('checkout.cartTotals.vatNoteSuffix'),
        continue: t('checkout.actions.continue'),
    };

    return {
        enrichedItems,
        subtotalValue,
        vat,
        shippingCost,
        total,
        labels,
    };
};