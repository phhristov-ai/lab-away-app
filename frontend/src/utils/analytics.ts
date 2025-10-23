type GAItem = {
  item_id: string;
  item_name: string;
  price: number;
  quantity: number;
  item_category?: string;
};

export function trackGAEvent(eventName: string, eventData: Record<string, any>) {
  console.log('[GA Event]', eventName, eventData);
  if (typeof window.gtag === 'function') {
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
    debug_mode: true
  });
}

export function trackRemoveFromCart(item: GAItem, currency = 'EUR') {
  trackGAEvent('remove_from_cart', {
    currency,
    value: item.price * item.quantity,
    items: [item],
    debug_mode: true
  });
}

export function trackViewItem(item: GAItem, currency = 'EUR') {
  trackGAEvent('view_item', {
    currency,
    value: item.price,
    items: [item],
    debug_mode: true
  });
}


// You can add more, like:
// trackBeginCheckout(), trackPurchase() etc.
