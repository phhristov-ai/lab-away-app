import React, { useState } from 'react';
import { ProductFullType } from '../../types/ProductFullType';

type AdminProductEditorProps = {
  product: ProductFullType;
  onProductUpdate: (updatedProduct: ProductFullType) => void;
};

const AdminProductEditor: React.FC<AdminProductEditorProps> = ({
  product,
  onProductUpdate,
}) => {
  const [name, setName] = useState(product.name);
  const [price, setPrice] = useState(product.price);
  const [description, setDescription] = useState(product.description);
  const [images, setImages] = useState<ProductImage[]>(product.images || []);

  const handleSave = () => {
    const updatedProduct: ProductFullType = {
      ...product,
      name,
      price,
      description,
      images,
    };

    onProductUpdate(updatedProduct);
  };

  return (
    <div className="admin-editor">
      <h3>Admin Editor</h3>

      <label>
        <span>Product Name</span>
        <input value={name} onChange={(e) => setName(e.target.value)} />
      </label>

      <label>
        <span>Price</span>
        <input
          type="number"
          value={price}
          onChange={(e) => setPrice(Number.parseFloat(e.target.value))}
        />
      </label>


      <label>
        <span>Description</span>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </label>

      <div className="image-management">
        <h4>Images</h4>
        {images.map((img, idx) => (
          <div key={img.imageUrlSmall + idx} style={{ marginBottom: '1rem' }}>
            <input
              value={img.imageUrlSmall}
              onChange={(e) => {
                const newImages = [...images];
                newImages[idx] = { ...newImages[idx], imageUrlSmall: e.target.value };
                setImages(newImages);
              }}
              placeholder="Image URL"
            />
            <button
              onClick={() => setImages(images.filter((_, i) => i !== idx))}
              style={{ marginLeft: '1rem' }}
            >
              Remove
            </button>
          </div>
        ))}
        <button
          onClick={() =>
            setImages([
              ...images,
              {
                id: crypto.randomUUID(),
                imageUrlSmall: '',
                imageUrlMedium: '',
                imageUrlLarge: '',
                order: images.length,
              },
            ])
          }
        >
          Add Image
        </button>
      </div>

      <button onClick={handleSave}>Save Changes</button>
    </div>
  );
};

export default AdminProductEditor;