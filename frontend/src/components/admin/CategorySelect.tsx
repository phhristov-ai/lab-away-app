import React from 'react';

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
    <select
      multiple
      value={selectedSlugs}
      onChange={onChange}
      style={{ minWidth: '200px', minHeight: '100px' }}
    >
      {allCategories.map(category => (
        <option key={category.slug} value={category.slug}>
          {category.name}
        </option>
      ))}
    </select>
  );
};

export default CategorySelect;
