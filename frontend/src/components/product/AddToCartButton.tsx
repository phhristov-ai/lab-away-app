import { useCart } from '../../context/CartContext';

interface AddToCartButtonProps {
  onClick: () => void;
  label: string;
  slug: string;
  name: string;
  image: string;
  price: number;
  quantity: number;
}

const AddToCartButton: React.FC<AddToCartButtonProps> = ({
  onClick,
  label,
  slug,
  name,
  image,
  price,
  quantity
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
    <button className="add-to-cart-button" onClick={handleAdd}>
      {label}
    </button>
  );
};

export default AddToCartButton;