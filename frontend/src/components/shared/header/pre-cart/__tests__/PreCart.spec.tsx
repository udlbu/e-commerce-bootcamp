import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import { cartItem, user } from '@src/test-tools/data';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import { waitFor, screen } from '@testing-library/react';
import PreCart from '@src/components/shared/header/pre-cart/PreCart';

const QUANTITY = 10;
// server mock
const server = setupServer(
  rest.get('/api/carts/:id', async (req, res, ctx) => {
    return res(
      ctx.json({
        id: '1',
        userId: '1',
        items: [{ ...cartItem, quantity: QUANTITY }],
      }),
    );
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());
describe('PreCart', () => {
  it('should PreCart component be created', async () => {
    const preCart = (
      <BrowserRouter>
        <PreCart />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(preCart, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    await waitFor(() => {
      expect(getByRole('items-counter')).toBeInTheDocument();
      expect(getByRole('items-counter').textContent).toEqual(QUANTITY + ' Items');
      expect(getByRole('cart-link')).toBeInTheDocument();
      expect(getByRole('total-amount')).toBeInTheDocument();
      expect(getByRole('total-amount').textContent).toEqual('$99.90');
      expect(getByRole('checkout-link')).toBeInTheDocument();
    });
  });

  it('should "Checkout" button be invisible when cart is empty', async () => {
    server.use(
      rest.get('/api/carts/:id', async (req, res, ctx) => {
        return res(ctx.json({}));
      }),
    );

    const preCart = (
      <BrowserRouter>
        <PreCart />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(preCart, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    await waitFor(() => {
      expect(getByRole('items-counter')).toBeInTheDocument();
      expect(getByRole('items-counter').textContent).toEqual('0 Items');
      expect(screen.queryByRole('cart-link')).toBeNull();
      expect(getByRole('total-amount')).toBeInTheDocument();
      expect(getByRole('total-amount').textContent).toEqual('$0.00');
      expect(screen.queryByRole('checkout-link')).toBeNull();
    });
  });
});
