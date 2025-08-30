import React, { createContext, useReducer, useContext, ReactNode, useMemo } from 'react';
import { trackAddToCart, trackGAEvent, trackRemoveFromCart } from '../utils/analytics';

type CartItem = {
  slug: string;
  name: string;
  price: number;
  quantity: number;
  image: string;
};

type CartState = {
  items: CartItem[];
};

type CartAction =
  | { type: 'ADD_ITEM'; payload: CartItem }
  | { type: 'REMOVE_ITEM'; payload: { slug: string } }
  | { type: 'CLEAR_CART' }
  | { type: 'UPDATE_QUANTITY'; payload: { slug: string; quantity: number } };


const initialState: CartState = { items: [] };

function cartReducer(state: CartState, action: CartAction): CartState {
  switch (action.type) {
    case 'ADD_ITEM':
      return handleAddItem(state, action.payload);
    case 'REMOVE_ITEM':
      return handleRemoveItem(state, action.payload.slug);
    case 'CLEAR_CART':
      return handleClearCart(state);
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
    item_category: 'Products',
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
  if (removedItem) {
    trackRemoveFromCart({
      item_id: removedItem.slug,
      item_name: removedItem.name,
      price: removedItem.price,
      quantity: removedItem.quantity,
    });
  }
  return {
    items: state.items.filter(item => item.slug !== slug),
  };
}

function handleClearCart(state: CartState): CartState {
  if (state.items.length > 0) {
    trackGAEvent('clear_cart', {
      items: state.items.map(item => ({
        item_id: item.slug,
        item_name: item.name,
        price: item.price,
        quantity: item.quantity,
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
