import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import CartHeader from '@src/components/cart-page/cart-header/CartHeader';

describe('CartHeader', () => {
  it('should CartHeader component be created', () => {
    // given
    const cartHeader = (
      <BrowserRouter>
        <table>
          <thead>
            <CartHeader />
          </thead>
        </table>
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(cartHeader);

    // then
    expect(getByRole('product-header').textContent).toEqual('PRODUCT');
    expect(getByRole('name-header').textContent).toEqual('NAME');
    expect(getByRole('unit-price-header').textContent).toEqual('UNIT PRICE');
    expect(getByRole('quantity-header').textContent).toEqual('QUANTITY');
    expect(getByRole('total-header').textContent).toEqual('TOTAL');
    expect(getByRole('trash-icon-header')).toBeInTheDocument();
  });
});
