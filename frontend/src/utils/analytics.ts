type GAItem = {
  item_id: string;
  item_name: string;
  price: number;
  quantity: number;
  item_category?: string;
};

export function trackGAEvent(eventName: string, eventData: Record<string, any>) {
  console.log('[GA Event]', eventName, eventData); // 👈 Add this line
  if (typeof window !== 'undefined' && window.gtag) {
    window.gtag('event', eventName, eventData);
  } else {
    console.warn('gtag not available:', eventName, eventData);
  }
}

export function trackAddToCart(item: GAItem, currency = 'EUR') {
  trackGAEvent('add_to_cart', {
    currency,
    value: item.price * item.quantity,
    items: [item],
  });
}

export function trackRemoveFromCart(item: GAItem, currency = 'EUR') {
  trackGAEvent('remove_from_cart', {
    currency,
    value: item.price * item.quantity,
    items: [item],
  });
}

export function trackViewItem(item: GAItem, currency = 'EUR') {
  trackGAEvent('view_item', {
    currency,
    value: item.price,
    items: [item],
  });
}


// You can add more, like:
// trackBeginCheckout(), trackPurchase() etc.
