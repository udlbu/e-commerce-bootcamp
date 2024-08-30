import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import CartItemRow from '../CartItemRow';
import { cartItem, offer, user } from '@src/test-tools/data';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import { act, waitFor } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import { ChangeItemQuantityRequest, RemoveItemFromCartRequest } from '@src/types/cart';
import '@src/test-tools/mocks';

// server mock
const server = setupServer(
  rest.get('/api/offers/:id', (req, res, ctx) => {
    return res(ctx.json(offer));
  }),
  rest.get('api/carts/:id', async (req, res, ctx) => {
    return res(ctx.json({}));
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());
describe('CartItemRow', () => {
  it('should CartItemRow component be created', async () => {
    // given
    const QUANTITY = 2;
    const component = (
      <BrowserRouter>
        <table>
          <tbody>
            <CartItemRow item={{ ...cartItem, quantity: QUANTITY }} />
          </tbody>
        </table>
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(component);

    // then
    await waitFor(() => {
      expect(getByRole('remove-item-btn')).toBeInTheDocument();
      expect(getByRole('product-img')).toHaveAttribute('src', cartItem.imageUrl);
      expect(getByRole('product-name').textContent).toEqual(cartItem.productName);
      expect(getByRole('price').textContent).toEqual(`$${cartItem.price}`);
      expect(getByRole('quantity')).toHaveAttribute('value', `${QUANTITY}`);
      expect(getByRole('total-amount').textContent).toEqual(`$19.98`); // quantity * price
    });
  });

  it('should quantity be incremented when user clicked "+" button and there is enough offers in the stock', async () => {
    // given
    const requests = [] as ChangeItemQuantityRequest[];
    server.use(
      rest.put('api/carts/change-quantity', async (req, res, ctx) => {
        requests.push(await req.json());
        return res(ctx.json({}));
      }),
    );
    const QUANTITY = 2;
    const component = (
      <BrowserRouter>
        <table>
          <tbody>
            <CartItemRow item={{ ...cartItem, quantity: QUANTITY }} />
          </tbody>
        </table>
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(component, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    await waitFor(() => {
      expect(getByRole('plus')).toBeInTheDocument();
      expect(getByRole('quantity')).toHaveAttribute('value', `${QUANTITY}`);
      expect(getByRole('total-amount').textContent).toEqual(`$19.98`); // 2 * price
    });

    // and when user clicked "plus"
    await act(() => {
      userEvent.click(getByRole('plus'));
    });

    // then
    await waitFor(() => {
      expect(requests.length).toEqual(1);
      expect(requests[0].offerId).toEqual(cartItem.offerId);
      expect(requests[0].userId).toEqual(user.id);
      expect(requests[0].quantity).toEqual(3);
    });
  });

  it('should quantity not be incremented when user clicked "+" button but there is not enough offers in the stock', async () => {
    // given
    const requests = [] as ChangeItemQuantityRequest[];
    server.use(
      rest.put('api/carts/change-quantity', async (req, res, ctx) => {
        requests.push(await req.json());
        return res(ctx.json({}));
      }),
    );
    const QUANTITY = 5;
    const component = (
      <BrowserRouter>
        <table>
          <tbody>
            <CartItemRow item={{ ...cartItem, quantity: QUANTITY }} />
          </tbody>
        </table>
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(component, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    await waitFor(() => {
      expect(getByRole('plus')).toBeInTheDocument();
      expect(getByRole('quantity')).toHaveAttribute('value', `${QUANTITY}`);
      expect(getByRole('total-amount').textContent).toEqual(`$49.95`); // 5 * price
    });

    // and when user clicked "plus"
    await act(() => {
      userEvent.click(getByRole('plus'));
    });

    // then
    await waitFor(() => {
      expect(requests.length).toEqual(0);
    });
  });

  it('should quantity be decremented when user clicked "-" button and there is more than 1 quantity item', async () => {
    // given
    const requests = [] as ChangeItemQuantityRequest[];
    server.use(
      rest.put('api/carts/change-quantity', async (req, res, ctx) => {
        requests.push(await req.json());
        return res(ctx.json({}));
      }),
    );
    const QUANTITY = 2;
    const component = (
      <BrowserRouter>
        <table>
          <tbody>
            <CartItemRow item={{ ...cartItem, quantity: QUANTITY }} />
          </tbody>
        </table>
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(component, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    await waitFor(() => {
      expect(getByRole('minus')).toBeInTheDocument();
      expect(getByRole('quantity')).toHaveAttribute('value', `${QUANTITY}`);
      expect(getByRole('total-amount').textContent).toEqual(`$19.98`); // 2 * price
    });

    // and when user clicked "minus"
    await act(() => {
      userEvent.click(getByRole('minus'));
    });

    // then
    await waitFor(() => {
      expect(requests.length).toEqual(1);
      expect(requests[0].offerId).toEqual(cartItem.offerId);
      expect(requests[0].userId).toEqual(user.id);
      expect(requests[0].quantity).toEqual(1);
    });
  });

  it('should quantity not be decremented when user clicked "-" button but there is only 1 quantity item', async () => {
    // given
    const requests = [] as ChangeItemQuantityRequest[];
    server.use(
      rest.put('api/carts/change-quantity', async (req, res, ctx) => {
        requests.push(await req.json());
        return res(ctx.json({}));
      }),
    );
    const QUANTITY = 1;
    const component = (
      <BrowserRouter>
        <table>
          <tbody>
            <CartItemRow item={{ ...cartItem, quantity: QUANTITY }} />
          </tbody>
        </table>
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(component, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    await waitFor(() => {
      expect(getByRole('minus')).toBeInTheDocument();
      expect(getByRole('quantity')).toHaveAttribute('value', `${QUANTITY}`);
      expect(getByRole('total-amount').textContent).toEqual(`$9.99`); // 1 * price
    });

    // and when user clicked "minus"
    await act(() => {
      userEvent.click(getByRole('minus'));
    });

    // then
    await waitFor(() => {
      expect(requests.length).toEqual(0);
    });
  });

  it('should delete item from cart when "Trash" icon button is clicked', async () => {
    // given
    const requests = [] as RemoveItemFromCartRequest[];
    server.use(
      rest.post('api/carts/remove-item', async (req, res, ctx) => {
        requests.push(await req.json());
        return res(ctx.json({}));
      }),
    );
    const QUANTITY = 1;
    const component = (
      <BrowserRouter>
        <table>
          <tbody>
            <CartItemRow item={{ ...cartItem, quantity: QUANTITY }} />
          </tbody>
        </table>
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(component, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    await waitFor(() => {
      expect(getByRole('remove-item-btn')).toBeInTheDocument();
    });

    // and when user clicked "minus"
    await act(() => {
      userEvent.click(getByRole('remove-item-btn'));
    });

    // then
    await waitFor(() => {
      expect(requests.length).toEqual(1);
      expect(requests[0].offerId).toEqual(cartItem.offerId);
      expect(requests[0].userId).toEqual(user.id);
    });
  });
});
