import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import PreCartItem from '@src/components/shared/header/pre-cart/pre-cart-item/PreCartItem';
import { cartItem, user } from '@src/test-tools/data';
import { RemoveItemFromCartRequest } from '@src/types/cart';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import { act } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';

const removeRequests = [] as RemoveItemFromCartRequest[];
// server mock
const server = setupServer(
  rest.post('/api/carts/remove-item', async (req, res, ctx) => {
    const item = await req.json();
    removeRequests.push(item);
    return res(ctx.json({}));
  }),
  rest.get('/api/carts/:id', async (req, res, ctx) => {
    return res(ctx.json({}));
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());
describe('PreCartItem', () => {
  it('should PreCartItem component be created', () => {
    const preCartItem = (
      <BrowserRouter>
        <PreCartItem item={cartItem} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(preCartItem);

    // then
    expect(getByRole('remove-btn')).toBeInTheDocument();
    expect(getByRole('cart-img')).toBeInTheDocument();
    expect(getByRole('cart-img')).toHaveAttribute('src', cartItem.imageUrl);
    expect(getByRole('product-name')).toBeInTheDocument();
    expect(getByRole('product-name').textContent).toEqual(cartItem.productName);
    expect(getByRole('quantity')).toBeInTheDocument();
    expect(getByRole('quantity').textContent).toEqual(
      cartItem.quantity + '\xa0x - $' + cartItem.price,
    );
  });

  it('should remove item from cart', async () => {
    const preCartItem = (
      <BrowserRouter>
        <PreCartItem item={cartItem} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(preCartItem, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    await act(async () => {
      await userEvent.click(getByRole('remove-btn'));
    });

    // then
    expect(removeRequests.length).toEqual(1);
    expect(removeRequests[0].userId).toEqual(user.id);
    expect(removeRequests[0].offerId).toEqual(cartItem.offerId);
  });
});
