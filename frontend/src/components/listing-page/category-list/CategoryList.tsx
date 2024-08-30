import React from 'react';
import { ProductCategory } from '@src/types/product';
import styles from './CategoryList.module.scss';

interface CategoryListProps {
  onCategoryChange: (category: string) => void;
  selectedCategory?: string;
}
const CategoryList: React.FC<CategoryListProps> = ({ selectedCategory, onCategoryChange }) => {
  return (
    <div className='single-widget category'>
      <h3 className='title'>Categories</h3>
      <ul className='category-list'>
        {Object.keys(ProductCategory).map((it) => {
          const category = ProductCategory[it as keyof typeof ProductCategory].toLowerCase();
          return (
            <li key={it}>
              <a
                role='categoryLink'
                onClick={() => onCategoryChange(it)}
                className={it === selectedCategory ? styles.active : ''}
              >
                {category}
              </a>
            </li>
          );
        })}
      </ul>
    </div>
  );
};

export default CategoryList;
