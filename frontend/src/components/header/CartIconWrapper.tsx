import { useCart } from "../../context/CartContext";
import CartIcon from "./CartIcon";


const CartIconWrapper = () => {
  const { state } = useCart();

  const cartCount = state.items.reduce((total, item) => total + item.quantity, 0);

  return <CartIcon cartCount={cartCount} />;
};

export default CartIconWrapper;