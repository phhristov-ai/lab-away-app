import { useCart } from '../../context/CartContext';
import { Category } from '../../services/categoriesService';
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
  categories
}) => {
  const { dispatch } = useCart();

  const handleAdd = () => {
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

  return (
    <button className={`add-to-cart-button ${variant}`} onClick={handleAdd}>
      {label}
    </button>
  );
};

export default AddToCartButton;