import { useCart } from '../../context/CartContext';
import { Category } from '../../services/category/categoriesService';
import './AddToCartButton.css';

interface AddToCartButtonProps {
  onClick: () => void;
  label: string;
  slug: string;
  name: string;
  image: string;
  price: number;
  quantity: number;
  variant?: 'primary' | 'secondary';
  categories: Category[];
  active: boolean | undefined
}

const AddToCartButton: React.FC<AddToCartButtonProps> = ({
  onClick,
  label,
  slug,
  name,
  image,
  price,
  quantity,
  variant = 'primary',
  categories,
  active
}) => {
  const { dispatch } = useCart();

  const handleAdd = () => {

    if (!active) {
      return;
    }
    
    dispatch({
      type: 'ADD_ITEM',
      payload: {
        slug,
        name,
        price,
        quantity,
        image,
        categories,
        title: name,
        product: '',
        subtotal: price * quantity
      },
    });
    onClick();
  };
  const isDisabled = !active;
  return (
    <button
      className={`add-to-cart-button ${variant} ${isDisabled ? 'disabled' : ''}`}
      onClick={handleAdd}
      disabled={!active}
      aria-disabled={!active}
    >
      {label}
    </button>
  );
};
export default AddToCartButton;