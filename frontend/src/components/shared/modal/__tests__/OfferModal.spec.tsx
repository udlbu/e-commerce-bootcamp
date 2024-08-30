import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import OfferModal from '@src/components/shared/modal/OfferModal';
import { offer, user } from '@src/test-tools/data';
import { act, waitFor, screen } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
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

describe('OfferModal', () => {
  it('should OfferModal component be created', () => {
    // given
    const offerModal = (
      <BrowserRouter>
        <OfferModal offer={offer} />
      </BrowserRouter>
    );

    // when
    const { container, getAllByRole, getByRole } = renderWithProviders(offerModal, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('closeBtn')).toBeInTheDocument();
    expect(getAllByRole('offerImage')[0]).toHaveAttribute('src', offer.product.imageUrl);
    expect(container.querySelector('h2')).toHaveTextContent(offer.product.name);
    expect(getByRole('stockQuantity')).toHaveTextContent(`${offer.quantity} items in stock`);
    expect(getByRole('offerProductDesc')).toHaveTextContent(offer.product.description);
    expect(container.querySelector('h3')).toHaveTextContent(offer.price);
    expect(getByRole('plus')).toBeInTheDocument();
    expect(getByRole('minus')).toBeInTheDocument();
  });

  it('should "quantity" value be incremented when user clicked "plus" button', async () => {
    // given
    const offerModal = (
      <BrowserRouter>
        <OfferModal offer={{ ...offer, quantity: '2' }} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerModal, {
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
    const offerModal = (
      <BrowserRouter>
        <OfferModal offer={{ ...offer, quantity: '2' }} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerModal, {
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
    const offerModal = (
      <BrowserRouter>
        <OfferModal offer={offer} />
      </BrowserRouter>
    );

    // when
    renderWithProviders(offerModal);

    // then
    expect(screen.queryByRole('quantity')).toBeNull();
    expect(screen.queryByRole('add-to-cart')).toBeNull();
  });

  it('should add item to cart with quantity when "Add to cart but is clicked"', async () => {
    // given
    const EXPECTED_ITEM_QUANTITY = 2;
    const offerModal = (
      <BrowserRouter>
        <OfferModal offer={{ ...offer, quantity: '2' }} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerModal, {
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
      await userEvent.click(getByRole('add-to-cart-modal-btn'));
    });

    // then
    expect(cart.length).toEqual(1);
    expect(cart[0].userId).toEqual(user.id);
    expect(cart[0].offerId).toEqual(offer.id);
    expect(cart[0].quantity).toEqual(EXPECTED_ITEM_QUANTITY);
  });
});
