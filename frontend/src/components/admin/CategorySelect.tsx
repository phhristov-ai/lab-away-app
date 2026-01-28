import React from 'react';
import './CategorySelect.css';

type Category = {
  slug: string;
  name: string;
};

type CategorySelectProps = {
  allCategories: Category[];
  selectedSlugs: string[];
  onChange: (event: React.ChangeEvent<HTMLSelectElement>) => void;
};

const CategorySelect: React.FC<CategorySelectProps> = ({
  allCategories,
  selectedSlugs,
  onChange,
}) => {
  return (
    <div className="category-select-wrapper">
      <label className="category-label" htmlFor="category-select">
        Categories
      </label>

      <select
        id="category-select"
        className="category-select"
        multiple
        value={selectedSlugs}
        onChange={onChange}
      >
        {allCategories.map(category => (
          <option key={category.slug} value={category.slug}>
            {category.name}
          </option>
        ))}
      </select>

      <span className="category-hint">
        Hold Ctrl (Cmd on Mac) to select multiple
      </span>
    </div>
  );
};

export default CategorySelect;
