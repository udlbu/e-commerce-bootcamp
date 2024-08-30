import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import ProductGallery from '@src/components/show-item-page/product-gallery/ProductGallery';
import { product } from '@src/test-tools/data';

describe('ProductGallery', () => {
  it('should ProductGallery component be created', () => {
    // given
    const breadcrumbs = (
      <BrowserRouter>
        <ProductGallery product={product} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(breadcrumbs);

    // then
    expect(container.querySelectorAll('img').length).toEqual(3);
    expect(container.querySelectorAll('img')[0]).toHaveAttribute('src', product.imageUrl);
    expect(container.querySelectorAll('img')[1]).toHaveAttribute('src', product.imageUrl);
    expect(container.querySelectorAll('img')[2]).toHaveAttribute('src', product.imageUrl);
  });
});
