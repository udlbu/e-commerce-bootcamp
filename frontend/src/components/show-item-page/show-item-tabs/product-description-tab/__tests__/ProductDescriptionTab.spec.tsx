import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import ProductDescriptionTab from '@src/components/show-item-page/show-item-tabs/product-description-tab/ProductDescriptionTab';
import { product } from '@src/test-tools/data';

describe('ProductDescriptionTab', () => {
  it('should ProductDescriptionTab component be created', () => {
    // given
    const productDescriptionTab = (
      <BrowserRouter>
        <ProductDescriptionTab product={product} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(productDescriptionTab);

    // then
    expect(container.querySelector('p').textContent).toEqual(product.description);
  });
});
