import React from 'react';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { ProductCategory } from '@src/types/product';
import CategoryList from '@src/components/listing-page/category-list/CategoryList';
import { fireEvent } from '@testing-library/react';

describe('Category List', () => {
  it('should CategoryList component be created', () => {
    // given
    const categoryList = (
      <BrowserRouter>
        <CategoryList onCategoryChange={jest.fn()} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(categoryList);

    // then
    expect(container.querySelector('.category-list')).toBeInTheDocument();
    expect(container.querySelector('.title').textContent).toEqual('Categories');
    expect(container.querySelectorAll('li').length).toEqual(Object.keys(ProductCategory).length);
    expect(container.querySelectorAll('.active').length).toEqual(0);
  });

  it('should highlight link with selected category', () => {
    // given
    const categoryList = (
      <BrowserRouter>
        <CategoryList onCategoryChange={jest.fn()} selectedCategory={'BEAUTY'} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(categoryList);

    // then
    expect(container.querySelectorAll('.active').length).toEqual(1);
    expect(container.querySelector('.active').textContent).toEqual('beauty');
  });

  it('should onCategoryChange be called when user changes products category', () => {
    // given
    const onCategoryChange = jest.fn();
    const categoryList = (
      <BrowserRouter>
        <CategoryList onCategoryChange={onCategoryChange} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(categoryList);

    // and category selected
    const firstCategory = container.querySelectorAll('a')[0];
    fireEvent.click(firstCategory);

    // then
    expect(onCategoryChange).toHaveBeenCalledTimes(1);
    expect(onCategoryChange).toHaveBeenCalledWith(firstCategory.textContent.toUpperCase());
  });
});
