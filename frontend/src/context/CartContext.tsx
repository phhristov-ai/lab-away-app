import React, { createContext, useReducer, useContext, ReactNode, useMemo } from 'react';

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
    case 'ADD_ITEM': {
      const existing = state.items.find(item => item.slug === action.payload.slug);
      if (existing) {
        return {
          items: state.items.map(item =>
            item.slug === action.payload.slug
              ? { ...item, quantity: item.quantity + action.payload.quantity }
              : item
          ),
        };
      }
      return { items: [...state.items, action.payload] };
    }
    case 'REMOVE_ITEM':
      return {
        items: state.items.filter(item => item.slug !== action.payload.slug),
      };
    case 'CLEAR_CART':
      return { items: [] };
    case 'UPDATE_QUANTITY':
      return {
        items: state.items.map(item =>
          item.slug === action.payload.slug
            ? { ...item, quantity: action.payload.quantity }
            : item
        ),
      };
    default:
      return state;
  }
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
