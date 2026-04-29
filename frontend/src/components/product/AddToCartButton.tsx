import { useCart } from '../../context/CartContext';
import { Category } from '../../services/category/categoriesService';
import './AddToCartButton.css';
import { useAddToCart } from './useAddToCart';

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
  const addToCart = useAddToCart();

  const handleAdd = () => {
    if (!active) return;

    addToCart({
      slug,
      name,
      price,
      quantity,
      image,
      categories,
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