import React from 'react';
import { useTranslation } from 'react-i18next';
import { Category } from '../../services/category/categoriesService';
import './FilterBar.css';

type FilterBarProps = {
  categories: Category[];
  onSelectCategory: (slug: string | null) => void;
  selectedCategory: string | null;
};

const FilterBar: React.FC<FilterBarProps> = ({ categories, onSelectCategory, selectedCategory }) => {
  const { t } = useTranslation();

  return (
    <div className="filters">
      <button
        key="all"
        className={`filter-button ${selectedCategory === null ? 'active' : ''}`}
        onClick={() => onSelectCategory(null)}
      >
        {t('shop.filters.all')}
      </button>

      {categories.map((category) => (
        <button
          key={category.slug}
          className={`filter-button ${selectedCategory === category.slug ? 'active' : ''}`}
          onClick={() => onSelectCategory(category.slug)}
        >
          {category.name}
        </button>
      ))}
    </div>
  );
};

export default FilterBar;
