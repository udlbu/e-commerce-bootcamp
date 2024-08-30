import React from 'react';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import { renderWithProviders } from '@src/test-tools/test-utils';
import ProductItem from '@src/components/shared/product-item/ProductItem';
import { offer, user } from '@src/test-tools/data';
import { act, screen } from '@testing-library/react';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import { userEvent } from '@testing-library/user-event';
import { AddItemToCartRequest } from '@src/types/cart';

const cart = [] as AddItemToCartRequest[];
// server mock
const server = setupServer(
  rest.post('/api/carts', async (req, res, ctx) => {
    const item = await req.json();
    cart.push(item);
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

describe('Product Item', () => {
  it('should ProductItem component be created', () => {
    // given
    const productItem = (
      <BrowserRouter>
        <ProductItem offer={offer} isHot={false} showModal={false} />
      </BrowserRouter>
    );

    // when
    const { getByRole, container } = renderWithProviders(productItem);

    // then
    const actions = container.querySelectorAll('.product-action a');
    expect(getByRole('productImage').getAttribute('src')).toEqual('image.jpg');
    expect(actions.length).toEqual(3);
    expect(actions[0].textContent).toEqual('Quick Shop');
    expect(actions[1].textContent).toEqual('Add to Wishlist');
    expect(actions[2].textContent).toEqual('Add to Compare');
    expect(container.querySelector('.product-action-2 a')).toBeNull();
    expect(getByRole('productName').textContent).toEqual('Iphone');
    expect(getByRole('offerPrice').textContent).toEqual('$9.99');
    expect(screen.queryByRole('hotLabel')).toBeNull();
    expect(container.querySelector('.modal-dialog')).toBeNull();
  });

  it('should show "hot" label when product is sellable in Hot Offer', () => {
    // given
    const productItem = (
      <BrowserRouter>
        <ProductItem offer={offer} isHot={true} showModal={false} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(productItem);

    // then
    expect(getByRole('hotLabel')).toBeInTheDocument();
  });

  it('should contain OfferModal when showModal flag is set to true', () => {
    // given
    const productItem = (
      <BrowserRouter>
        <ProductItem offer={offer} isHot={false} showModal={true} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(productItem);

    // then
    expect(container.querySelector('.modal-dialog')).toBeInTheDocument();
  });

  it('should contain "Add to cart" button when user is logged in and user is not owner of the offer', () => {
    // given
    const productItem = (
      <BrowserRouter>
        <ProductItem offer={offer} isHot={false} showModal={true} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(productItem, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(container.querySelector('.product-action-2 a').textContent).toEqual('Add to cart');
  });

  it('should not contain "Add to cart" button when user is owner of the offer', () => {
    // given
    const userId = '101';
    const productItem = (
      <BrowserRouter>
        <ProductItem offer={{ ...offer, userId }} isHot={false} showModal={true} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(productItem, {
      preloadedState: {
        user: {
          ...user,
          id: userId,
        },
      },
    });

    // then
    expect(container.querySelector('.product-action-2 a')).toBeNull();
  });

  it('should add the offer to the cart when user clicked "Add to cart" button', async () => {
    // given
    const EXPECTED_ITEM_QUANTITY = 1;
    const productItem = (
      <BrowserRouter>
        <ProductItem offer={offer} isHot={false} showModal={true} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(productItem, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    await act(async () => {
      await userEvent.click(getByRole('add-to-cart-btn'));
    });

    // then
    expect(cart.length).toEqual(1);
    expect(cart[0].userId).toEqual(user.id);
    expect(cart[0].offerId).toEqual(offer.id);
    expect(cart[0].quantity).toEqual(EXPECTED_ITEM_QUANTITY);
  });
});
