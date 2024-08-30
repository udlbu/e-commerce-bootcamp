import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import CartSummary from '@src/components/cart-page/cart-summary/CartSummary';
import { cartItem } from '@src/test-tools/data';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import { act, waitFor } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import { SubmitOrderRequest } from '@src/types/order';

// server mock
const requests = [] as SubmitOrderRequest[];
const server = setupServer(
  rest.post('/api/orders', async (req, res, ctx) => {
    requests.push(await req.json());
    return res(ctx.json({}));
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());
describe('CartSummary', () => {
  it('should CartSummary component be created', () => {
    // given
    const EXPECTED_TOTAL_CART_VALUE = '$54.00'; // 2*12 + 3*10
    const cartSummary = (
      <BrowserRouter>
        <CartSummary onOrderSubmitSuccess={jest.fn()} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(cartSummary, {
      preloadedState: {
        cart: {
          id: '1',
          userId: '1',
          items: [
            { ...cartItem, quantity: 2, price: '12' },
            { ...cartItem, quantity: 3, price: '10' },
          ],
        },
      },
    });

    // then
    expect(getByRole('total-net-amount').textContent).toEqual(EXPECTED_TOTAL_CART_VALUE);
    expect(getByRole('total-gross-amount').textContent).toEqual(EXPECTED_TOTAL_CART_VALUE);
    expect(getByRole('submit-order-btn')).toBeInTheDocument();
    expect(getByRole('continue-shopping-btn')).toBeInTheDocument();
  });

  it('should submit order when user clicked "Checkout" button', async () => {
    // given
    const onOrderSubmitSuccess = jest.fn();
    const cartSummary = (
      <BrowserRouter>
        <CartSummary onOrderSubmitSuccess={onOrderSubmitSuccess} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(cartSummary, {
      preloadedState: {
        cart: {
          id: '1',
          userId: '1',
          items: [{ ...cartItem, quantity: 1, price: '12' }],
        },
      },
    });

    // and
    await act(() => {
      userEvent.click(getByRole('submit-order-btn'));
    });

    // then
    await waitFor(() => {
      expect(onOrderSubmitSuccess).toBeCalledTimes(1);
      expect(requests.length).toEqual(1);
      expect(requests[0].cartId).toEqual('1');
      expect(requests[0].deliveryMethod).toEqual('DHL');
      expect(requests[0].paymentMethod).toEqual('TRANSFER');
    });
  });
});
