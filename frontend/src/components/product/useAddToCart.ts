import { useCart } from "../../context/CartContext";
import { Category } from "../../services/category/categoriesService";

export const useAddToCart = () => {
  const { dispatch } = useCart();

  return (item: {
    slug: string;
    name: string;
    price: number;
    quantity: number;
    image: string;
    categories: Category[];
  }) => {
    dispatch({
      type: 'ADD_ITEM',
      payload: {
        ...item,
        title: item.name,
        product: '',
        subtotal: item.price * item.quantity,
      },
    });
  };
};