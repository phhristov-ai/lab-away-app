import { sendGAEvent } from "../context/consent/consentUtils";
import { Category } from "../services/category/categoriesService";

export type GAItem = {
  item_id: string;
  item_name: string;
  price: number;
  quantity: number;
  item_category?: string;
  item_category2?: string;
};

export function trackGAEvent(eventName: string, eventData: Record<string, any>) {
  sendGAEvent(eventName, eventData);
}

export function trackAddToCart(item: GAItem, currency = 'EUR') {
  trackGAEvent('add_to_cart', {
    currency,
    value: item.price * item.quantity,
    items: [
      {
        item_id: item.item_id,
        item_name: item.item_name,
        price: item.price,
        quantity: item.quantity,
        item_category: item.item_category,
        item_category2: item.item_category2,
      },
    ],
    debug_mode: true,
  });
}

export function trackRemoveFromCart(item: GAItem, currency = 'EUR') {
  trackGAEvent('remove_from_cart', {
    currency,
    value: item.price * item.quantity,
    items: [
      {
        item_id: item.item_id,
        item_name: item.item_name,
        price: item.price,
        quantity: item.quantity,
        item_category: item.item_category,
        item_category2: item.item_category2,
      },
    ],
    debug_mode: true,
  });
}

export function trackViewItem(item: GAItem, currency = 'EUR') {
  trackGAEvent('view_item', {
    currency,
    value: item.price,
    items: [
      {
        item_id: item.item_id,
        item_name: item.item_name,
        price: item.price,
        quantity: item.quantity,
        item_category: item.item_category,
        item_category2: item.item_category2,
      },
    ],
    debug_mode: true,
  });
}

export function trackViewCart(items: GAItem[], currency = 'EUR') {
  trackGAEvent('view_cart', {
    currency,
    value: items.reduce((sum, i) => sum + i.price * i.quantity, 0),
    items: items.map(i => ({
      item_id: i.item_id,
      item_name: i.item_name,
      price: i.price,
      quantity: i.quantity,
      item_category: i.item_category,
      item_category2: i.item_category2,
    })),
    debug_mode: true,
  });
}

export function trackBeginCheckout(items: GAItem[], currency = 'EUR') {
  trackGAEvent('begin_checkout', {
    currency,
    value: items.reduce((sum, i) => sum + i.price * i.quantity, 0),
    items,
  });
}

export function trackAddShippingInfo(items: GAItem[], currency = 'EUR') {
  trackGAEvent('add_shipping_info', {
    currency,
    value: items.reduce((sum, i) => sum + i.price * i.quantity, 0),
    items,
  });
}

export function trackAddPaymentInfo(items: GAItem[], payment_type: string, currency = 'EUR') {
  trackGAEvent('add_payment_info', {
    currency,
    payment_type,
    value: items.reduce((sum, i) => sum + i.price * i.quantity, 0),
    items,
  });
}

export function trackPurchase(orderId: string, items: GAItem[], total: number, currency = 'EUR') {
  trackGAEvent('purchase', {
    transaction_id: orderId,
    affiliation: 'Lab-Away Online Store',
    currency,
    value: total,
    items,
  });
}

export function trackPurchaseFailed(
  method: string,
  total: number,
  reason: string = 'unknown_error',
  currency = 'EUR'
) {
  trackGAEvent('purchase_failed', {
    affiliation: 'Lab-Away Online Store',
    method,
    currency,
    value: total,
    reason,
  });
}

export function trackSubscribe(method = 'email_form') {
  trackGAEvent('subscribe', { method, status: 'success' });
}

export function trackViewBlog() {
  trackGAEvent('view_blog', {
    page_title: 'Blog',
    section: 'blog',
  });
}

export function trackViewBlogPost(
  postId: string,
  title: string,
  categories?: string[] | Category[],
  author?: string
) {
  const categoryArray = Array.isArray(categories)
    ? categories.map((c: any) => c.name || c)
    : [];

  const categoryParams: Record<string, string> = {};
  let index = 1;

  for (const cat of categoryArray) {
    categoryParams[`category${index}`] = cat;
    index++;
  }

  trackGAEvent('view_blog_post', {
    page_title: title,
    post_id: postId,
    author,
    ...categoryParams,
  });
}


export function trackScrollBlogPost(postId: string, title: string, scrollDepth: number) {
  trackGAEvent('scroll_blog_post', {
    page_title: title,
    post_id: postId,
    scroll_depth: scrollDepth,
  });
}

export function trackShareBlogPost(postId: string, title: string, method: 'copy_link' | 'facebook' | 'twitter' | 'email') {
  trackGAEvent('share_blog_post', {
    page_title: title,
    post_id: postId,
    method,
  });
}

export function trackClickBlogToProduct(postId: string, title: string, productId: string, productName: string) {
  trackGAEvent('click_blog_to_product', {
    page_title: title,
    post_id: postId,
    product_id: productId,
    product_name: productName,
  });
}



