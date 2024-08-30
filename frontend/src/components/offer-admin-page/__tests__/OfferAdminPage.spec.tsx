import React from 'react';

import { act, fireEvent, screen, waitFor } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import { renderWithProviders } from '@src/test-tools/test-utils';
import OfferAdminPage from '@src/components/offer-admin-page/OfferAdminPage';
import { AddOfferRequest } from '@src/types/offer';
import { offer, product, user } from '@src/test-tools/data';
import '@src/test-tools/mocks';

// buttons
const addOfferBtn = 'button[role="addBtn"]';
const updateOfferBtn = 'button[role="updateBtn"]';
const editOfferBtn = 'a[role="editBtn"]';
const cancelProductBtn = 'button[role="cancelBtn"]';
const incrementQuantityBtn = 'button[role="incrementQuantity"]';
const decrementQuantityBtn = 'button[role="decrementQuantity"]';

// test data
const addOffersRequests = [] as AddOfferRequest[];

const offers = [offer];

// server mock
const server = setupServer(
  rest.get('/api/products', (req, res, ctx) => {
    return res(
      ctx.json({
        products: [product],
        total: 1,
      }),
    );
  }),
  rest.get('/api/offers/user/search', (req, res, ctx) => {
    return res(
      ctx.json({
        offers: [],
        total: 0,
      }),
    );
  }),
  rest.post('/api/offers', (req, res, ctx) => {
    req.json().then((data) => {
      addOffersRequests.push(data);
    });
    return res(ctx.json([]));
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());

describe('Offer Admin Page', () => {
  it('should OfferAdminPage component be created', () => {
    // given
    const offerAdminPage = (
      <BrowserRouter>
        <OfferAdminPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerAdminPage, {
      preloadedState: {
        user,
      },
    });

    // then
    expect(getByRole('product')).toBeInTheDocument();
    expect(getByRole('price')).toBeInTheDocument();
    expect(getByRole('quantity')).toBeInTheDocument();
    expect(getByRole('offersList')).toBeInTheDocument();
    expect(getByRole('addBtn')).toBeDisabled();
  });

  it('should add button be enabled when user enters required field', async () => {
    // given
    const offerAdminPage = (
      <BrowserRouter>
        <OfferAdminPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerAdminPage, {
      preloadedState: {
        user,
      },
    });

    // wait until product list is fetched
    await waitFor(() => {
      expect(getByRole('product').textContent).toContain('Iphone');
    });

    // fill the form
    fireEvent.change(getByRole('product'), { target: { value: 1 } });
    fireEvent.change(getByRole('price'), { target: { value: '19.90' } });

    // then add button should become enabled
    await waitFor(() => {
      expect(getByRole('addBtn')).toBeEnabled();
    });
  });

  it('should add offer by calling rest API when AddBtn is clicked', async () => {
    // given
    const offerAdminPage = (
      <BrowserRouter>
        <OfferAdminPage />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = renderWithProviders(offerAdminPage, {
      preloadedState: {
        user,
      },
    });

    // wait until product list is fetched
    await waitFor(() => {
      expect(getByRole('product').textContent).toContain('Iphone');
    });

    // fill the form
    fireEvent.change(getByRole('product'), { target: { value: 1 } });
    fireEvent.change(getByRole('price'), { target: { value: '19.90' } });

    // and when offer is added
    await act(async () => {
      await userEvent.click(container.querySelector(addOfferBtn));
    });

    // then
    expect(addOffersRequests.length).toEqual(1);
    expect(addOffersRequests[0].userId).toEqual('1000');
    expect(addOffersRequests[0].price).toEqual('19.90');
    expect(addOffersRequests[0].quantity).toEqual('1');
    expect(addOffersRequests[0].productId).toEqual('1');

    // and field should be set to default value
    expect(getByRole('price')).toHaveAttribute('value', '');
    expect(getByRole('quantity').textContent).toEqual('1');
  });

  it('should display error when a value in the fields is not valid', async () => {
    // given
    const offerAdminPage = (
      <BrowserRouter>
        <OfferAdminPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offerAdminPage, {
      preloadedState: {
        user,
      },
    });
    fireEvent.change(getByRole('product'), { target: { value: ' ' } });
    fireEvent.change(getByRole('price'), { target: { value: ' ' } });

    // then
    await waitFor(() => {
      expect(getByRole('productError')).toHaveTextContent('Field is required');
      expect(getByRole('priceError')).toHaveTextContent('Field is required and must be valid');
    });
  });

  it('should modify existing offer', async () => {
    // given
    let modifyOffersRequests = 0;
    server.use(
      rest.get('/api/offers/user/search', (req, res, ctx) => {
        return res(
          ctx.json({
            offers,
            total: offers.length,
          }),
        );
      }),

      rest.put('/api/offers', (req, res, ctx) => {
        req.json().then(() => {
          modifyOffersRequests++;
        });
        return res(ctx.json([]));
      }),
    );

    const offerAdminPage = (
      <BrowserRouter>
        <OfferAdminPage />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = renderWithProviders(offerAdminPage, {
      preloadedState: {
        user,
      },
    });

    // wait for offer to be fetched
    await waitFor(() => {
      expect(screen.queryByRole('offerRow')).toBeInTheDocument();
    });

    // then click edit button
    await act(async () => {
      await userEvent.click(container.querySelector(editOfferBtn));
    });

    // and expect data to be loaded into fields
    expect(getByRole('product')).toHaveTextContent('Iphone');
    expect(getByRole('price')).toHaveAttribute('value', '9.99');
    expect(getByRole('quantity')).toHaveTextContent('5');
    expect(getByRole('productName').textContent).toEqual('Iphone');
    expect(getByRole('ean').textContent).toEqual('89712302');
    expect(getByRole('category').textContent).toEqual('Electronics');
    expect(getByRole('description').textContent).toEqual('Iphone description');
    expect(getByRole('loaded-image')).toHaveAttribute('src', 'image.jpg');
    expect(getByRole('updateBtn')).toBeInTheDocument();
    expect(getByRole('cancelBtn')).toBeInTheDocument();
    expect(screen.queryByRole('addBtn')).toBeNull();

    // then change some field value
    fireEvent.change(getByRole('price'), { target: { value: '1699.00' } });

    // when update button is clicked
    await act(async () => {
      await userEvent.click(container.querySelector(updateOfferBtn));
    });

    // then field should be set to default value
    expect(getByRole('price')).toHaveAttribute('value', '');
    expect(getByRole('quantity').textContent).toEqual('1');
    expect(screen.queryByRole('updateBtn')).toBeNull();
    expect(screen.queryByRole('cancelBtn')).toBeNull();
    expect(getByRole('addBtn')).toBeInTheDocument();

    // and modify request is handled
    expect(modifyOffersRequests).toEqual(1);
  });

  it('should clear all field when user clicked "Cancel" in context of offer modification', async () => {
    // given
    server.use(
      rest.get('/api/offers/user/search', (req, res, ctx) => {
        return res(
          ctx.json({
            offers,
            total: offers.length,
          }),
        );
      }),
    );

    const offerAdminPage = (
      <BrowserRouter>
        <OfferAdminPage />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = renderWithProviders(offerAdminPage, {
      preloadedState: {
        user,
      },
    });

    // wait for offer to be fetched
    await waitFor(() => {
      expect(screen.queryByRole('offerRow')).toBeInTheDocument();
    });

    // then click edit button
    await act(async () => {
      await userEvent.click(container.querySelector(editOfferBtn));
    });

    // and expect data to be loaded into fields
    expect(getByRole('product')).toHaveTextContent('Iphone');
    expect(getByRole('price')).toHaveAttribute('value', '9.99');
    expect(getByRole('quantity')).toHaveTextContent('5');
    expect(getByRole('productName').textContent).toEqual('Iphone');
    expect(getByRole('ean').textContent).toEqual('89712302');
    expect(getByRole('category').textContent).toEqual('Electronics');
    expect(getByRole('description').textContent).toEqual('Iphone description');
    expect(getByRole('loaded-image')).toHaveAttribute('src', 'image.jpg');
    expect(getByRole('updateBtn')).toBeInTheDocument();
    expect(getByRole('cancelBtn')).toBeInTheDocument();
    expect(screen.queryByRole('addBtn')).toBeNull();

    // when cancel button is clicked
    await act(async () => {
      await userEvent.click(container.querySelector(cancelProductBtn));
    });

    // then field should be set to default value
    expect(getByRole('price')).toHaveAttribute('value', '');
    expect(getByRole('quantity').textContent).toEqual('1');
    expect(screen.queryByRole('updateBtn')).toBeNull();
    expect(screen.queryByRole('cancelBtn')).toBeNull();
    expect(getByRole('addBtn')).toBeInTheDocument();
  });

  it('should increment quantity when "plus" button is clicked and decrement when "minus" button is clicked', async () => {
    // given
    const offerAdminPage = (
      <BrowserRouter>
        <OfferAdminPage />
      </BrowserRouter>
    );
    const { container, getByRole } = renderWithProviders(offerAdminPage, {
      preloadedState: {
        user,
      },
    });

    // when
    await act(async () => {
      await userEvent.click(container.querySelector(incrementQuantityBtn));
    });

    // then
    await waitFor(() => {
      expect(getByRole('quantity').textContent).toEqual('2');
    });

    // and when decrement is clicked
    await act(async () => {
      await userEvent.click(container.querySelector(decrementQuantityBtn));
    });

    // then
    await waitFor(() => {
      expect(getByRole('quantity').textContent).toEqual('1');
    });
  });
});
