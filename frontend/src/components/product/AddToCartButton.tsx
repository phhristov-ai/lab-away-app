import { useCart } from '../../context/CartContext';
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
}

const AddToCartButton: React.FC<AddToCartButtonProps> = ({
  onClick,
  label,
  slug,
  name,
  image,
  price,
  quantity,
  variant = 'primary'
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