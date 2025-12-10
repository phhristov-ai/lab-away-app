import React, { createContext, useReducer, useContext, ReactNode, useMemo } from 'react';
import { trackAddToCart, trackGAEvent, trackRemoveFromCart } from '../utils/analytics';
import { Category } from '../services/categoriesService';
import { CartItem } from '../types/CartItem';


type CartState = {
  items: CartItem[];
};

type CartAction =
  | { type: 'ADD_ITEM'; payload: CartItem }
  | { type: 'REMOVE_ITEM'; payload: { slug: string; categories: Category[] } }
  | { type: 'CLEAR_CART'; payload: { reason: 'user' | 'purchase' | 'system' } }
  | { type: 'UPDATE_QUANTITY'; payload: { slug: string; quantity: number; categories: Category[] } };

const initialState: CartState = { items: [] };

function cartReducer(state: CartState, action: CartAction): CartState {
  switch (action.type) {
    case 'ADD_ITEM':
      return handleAddItem(state, action.payload);
    case 'REMOVE_ITEM':
      return handleRemoveItem(state, action.payload.slug);
    case 'CLEAR_CART':
      return handleClearCart(state, action.payload.reason);
    case 'UPDATE_QUANTITY':
      return handleUpdateQuantity(state, action.payload.slug, action.payload.quantity);
    default:
      return state;
  }
}

function handleAddItem(state: CartState, payload: CartItem): CartState {
  const existing = state.items.find(item => item.slug === payload.slug);

  trackAddToCart({
    item_id: payload.slug,
    item_name: payload.name,
    price: payload.price,
    quantity: payload.quantity,
    item_category: payload.categories[0].name,
    item_category2: payload.categories[1].name,
  });

  if (existing) {
    return {
      items: state.items.map(item =>
        item.slug === payload.slug
          ? { ...item, quantity: item.quantity + payload.quantity }
          : item
      ),
    };
  }
  return { items: [...state.items, payload] };
}

function handleRemoveItem(state: CartState, slug: string): CartState {
  const removedItem = state.items.find(item => item.slug === slug);
  console.log(removedItem);
  if (removedItem) {
    trackRemoveFromCart({
      item_id: removedItem.slug,
      item_name: removedItem.name,
      price: removedItem.price,
      quantity: removedItem.quantity,
      item_category: removedItem.categories[0].name,
      item_category2: removedItem.categories[1].name,
    });
  }
  return {
    items: state.items.filter(item => item.slug !== slug),
  };
}

function handleClearCart(state: CartState, reason: 'user' | 'purchase' | 'system'): CartState {
  if (state.items.length > 0 && reason === 'user') {
    trackGAEvent('clear_cart', {
      items: state.items.map(item => ({
        item_id: item.slug,
        item_name: item.name,
        price: item.price,
        quantity: item.quantity,
        item_category: item.categories[0].name,
        item_category2: item.categories[1].name,
      })),
    });
  }
  return { items: [] };
}

function handleUpdateQuantity(state: CartState, slug: string, quantity: number): CartState {
  const item = state.items.find(item => item.slug === slug);
  if (item && item.quantity !== quantity) {
    trackGAEvent('update_cart_quantity', {
      item_id: item.slug,
      item_name: item.name,
      price: item.price,
      old_quantity: item.quantity,
      new_quantity: quantity,
      item_category: item.categories[0]?.name,
      item_category2: item.categories[1]?.name,
    });
  }

  return {
    items: state.items.map(item =>
      item.slug === slug ? { ...item, quantity } : item
    ),
  };
}

const CartContext = createContext<{
  state: CartState;
  dispatch: React.Dispatch<CartAction>;
}>({ state: initialState, dispatch: () => null });

export const useCart = () => useContext(CartContext);

const getInitialCartState = (): CartState => {
  const stored = localStorage.getItem('cart');
  return stored ? JSON.parse(stored) : { items: [] };
};

export const CartProvider = ({ children }: { children: ReactNode }) => {
  const [state, dispatch] = useReducer(cartReducer, undefined, getInitialCartState);

  React.useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(state));
  }, [state]);

  const contextValue = useMemo(() => ({ state, dispatch }), [state, dispatch]);

  return (
    <CartContext.Provider value={contextValue}>
      {children}
    </CartContext.Provider>
  );
};
