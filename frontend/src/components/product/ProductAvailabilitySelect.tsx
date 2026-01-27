interface ProductAvailabilitySelectProps {
  enabled: boolean;
  onChange: (value: boolean) => void;
}

const ProductAvailabilitySelect: React.FC<ProductAvailabilitySelectProps> = ({
  enabled,
  onChange
}) => {
  return (
    <div className="product-availability">
      <label htmlFor="availability">Availability</label>
      <select
        id="availability"
        value={enabled ? 'in_stock' : 'out_of_stock'}
        onChange={(e) => onChange(e.target.value === 'in_stock')}
      >
        <option value="in_stock">In stock</option>
        <option value="out_of_stock">Out of stock</option>
      </select>
    </div>
  );
};

export default ProductAvailabilitySelect;