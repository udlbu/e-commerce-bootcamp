import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import { cartItem, user } from '@src/test-tools/data';
import { screen } from '@testing-library/react';
import SearchBar from '@src/components/shared/header/search-bar/SearchBar';

describe('SearchBar', () => {
  it('should SearchBar component be created', async () => {
    const searchBar = (
      <BrowserRouter>
        <SearchBar />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(searchBar, {
      preloadedState: {
        user: {
          ...user,
        },
        cart: {
          id: '1',
          userId: user.id,
          items: [
            { ...cartItem, id: '1', quantity: 2, price: '10.00' },
            { ...cartItem, id: '2', quantity: 3, price: '4.00' },
          ],
        },
      },
    });

    // then
    expect(getByRole('search-bar-component')).toBeInTheDocument();
    expect(getByRole('favourite-icon')).toBeInTheDocument();
    expect(getByRole('user-icon')).toBeInTheDocument();
    expect(getByRole('pre-cart-component')).toBeInTheDocument();
    expect(getByRole('total-items-count').textContent).toEqual('5');
  });

  it('should not display user, favourite and ... icon when user is not logged in', async () => {
    const searchBar = (
      <BrowserRouter>
        <SearchBar />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(searchBar);

    // then
    expect(getByRole('search-bar-component')).toBeInTheDocument();
    expect(screen.queryByRole('favourite-icon')).toBeNull();
    expect(screen.queryByRole('user-icon')).toBeNull();
    expect(screen.queryByRole('pre-cart-component')).toBeNull();
  });
});
