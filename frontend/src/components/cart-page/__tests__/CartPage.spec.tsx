import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import CartPage from '@src/components/cart-page/CartPage';
import { cartItem, offer } from '@src/test-tools/data';
import { act, screen, waitFor } from '@testing-library/react';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import { userEvent } from '@testing-library/user-event';

const server = setupServer(
  rest.post('/api/orders', async (req, res, ctx) => {
    return res(ctx.json({}));
  }),
  rest.get('/api/offers/:id', (req, res, ctx) => {
    return res(ctx.json(offer));
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
describe('CartPage', () => {
  it('should CartPage component be created', () => {
    // given
    const cartPage = (
      <BrowserRouter>
        <CartPage />
      </BrowserRouter>
    );

    // when
    const { getAllByRole } = renderWithProviders(cartPage, {
      preloadedState: {
        cart: {
          id: '1',
          userId: '1',
          items: [
            { ...cartItem, id: '1' },
            { ...cartItem, id: '2' },
          ],
        },
      },
    });

    // then
    expect(getAllByRole('cart-item-row').length).toEqual(2);
  });

  it('should display "Your cart is empty" message when there are no items in the cart', () => {
    // given
    const cartPage = (
      <BrowserRouter>
        <CartPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(cartPage, {
      preloadedState: {
        cart: {
          id: '1',
          userId: '1',
          items: [],
        },
      },
    });

    // then
    expect(screen.queryAllByRole('cart-item-row').length).toEqual(0);
    expect(getByRole('empty-cart').textContent).toEqual('Your cart is empty');
  });

  it('should display "Thank you page" when user has successfully submit an order', async () => {
    // given
    server.use(
      rest.get('/api/offers/:id', (req, res, ctx) => {
        return res(ctx.json({}));
      }),
    );
    const cartPage = (
      <BrowserRouter>
        <CartPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(cartPage, {
      preloadedState: {
        cart: {
          id: '1',
          userId: '1',
          items: [{ ...cartItem, quantity: 1, price: '12' }],
        },
      },
    });

    await act(() => {
      userEvent.click(getByRole('submit-order-btn'));
    });

    // then
    await waitFor(() => {
      expect(getByRole('thank-you-page')).toBeInTheDocument();
    });
  });
});
