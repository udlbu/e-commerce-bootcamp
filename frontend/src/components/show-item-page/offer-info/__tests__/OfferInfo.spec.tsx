import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import OfferInfo from '@src/components/show-item-page/offer-info/OfferInfo';
import { offer, user } from '@src/test-tools/data';
import { userEvent } from '@testing-library/user-event';
import { act, waitFor, screen } from '@testing-library/react';
import { AddItemToCartRequest } from '@src/types/cart';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import '@src/test-tools/mocks';

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
describe('OfferInfo', () => {
  it('should OfferInfo component be created', () => {
    // given
    const offerInfo = (
      <BrowserRouter>
        <OfferInfo offer={offer} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerInfo, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('productName')).toHaveTextContent(offer.product.name);
    expect(getByRole('priceDiscount')).toHaveTextContent(offer.price);
    expect(getByRole('price')).toHaveTextContent(offer.price);
    expect(getByRole('quantity')).toHaveAttribute('value', '1');
    expect(getByRole('add-to-cart')).toBeInTheDocument();
    expect(getByRole('description')).toHaveTextContent(offer.product.description);
    expect(getByRole('productCategory')).toHaveTextContent(offer.product.category.toLowerCase());
    expect(getByRole('availability').textContent).toEqual('Availability:\xa05 Products In Stock');
  });

  it('should "quantity" value be incremented when user clicked "plus" button', async () => {
    // given
    const offerInfo = (
      <BrowserRouter>
        <OfferInfo offer={{ ...offer, quantity: '2' }} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerInfo, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('quantity')).toHaveAttribute('value', '1');

    // and when user clicked "plus"
    await act(() => {
      userEvent.click(getByRole('plus'));
    });

    // then
    await waitFor(() => {
      expect(getByRole('quantity')).toHaveAttribute('value', '2');
    });

    // and when user clicker "plus" button again then quantity value should remain the same because there are only 2 items available
    await act(() => {
      userEvent.click(getByRole('plus'));
    });

    expect(getByRole('quantity')).toHaveAttribute('value', '2');
  });

  it('should "quantity" value be decremented when user clicked "minus" button', async () => {
    // given
    const offerInfo = (
      <BrowserRouter>
        <OfferInfo offer={{ ...offer, quantity: '2' }} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerInfo, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });
    await act(() => {
      userEvent.click(getByRole('plus'));
    });
    await waitFor(() => {
      expect(getByRole('quantity')).toHaveAttribute('value', '2');
    });

    // and when user clicked "minus" button
    await act(() => {
      userEvent.click(getByRole('minus'));
    });

    // then
    await waitFor(() => {
      expect(getByRole('quantity')).toHaveAttribute('value', '1');
    });

    // and when user clicked "minus" button again the quantity value should remain 1 because there is no option to buy 0 items
    await act(() => {
      userEvent.click(getByRole('minus'));
    });

    await waitFor(() => {
      expect(getByRole('quantity')).toHaveAttribute('value', '1');
    });
  });

  it('should "Add to cart" and "quantity" buttons be invisible when user is not logged in', () => {
    // given
    const offerInfo = (
      <BrowserRouter>
        <OfferInfo offer={offer} />
      </BrowserRouter>
    );

    // when
    renderWithProviders(offerInfo);

    // then
    expect(screen.queryByRole('quantity')).toBeNull();
    expect(screen.queryByRole('add-to-cart')).toBeNull();
  });

  it('should add item to cart with quantity when "Add to cart but is clicked"', async () => {
    // given
    const EXPECTED_ITEM_QUANTITY = 2;
    const offerInfo = (
      <BrowserRouter>
        <OfferInfo offer={{ ...offer, quantity: '2' }} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerInfo, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // and increment by 1
    await act(() => {
      userEvent.click(getByRole('plus'));
    });
    await waitFor(() => {
      expect(getByRole('quantity')).toHaveAttribute('value', '2');
    });

    // and "Add to cart is clicked"
    await act(async () => {
      await userEvent.click(getByRole('add-to-cart-offer-details-btn'));
    });

    // then
    expect(cart.length).toEqual(1);
    expect(cart[0].userId).toEqual(user.id);
    expect(cart[0].offerId).toEqual(offer.id);
    expect(cart[0].quantity).toEqual(EXPECTED_ITEM_QUANTITY);
  });
});
