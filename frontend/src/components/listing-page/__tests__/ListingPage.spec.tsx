import React from 'react';

import { act, waitFor } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { SearchOffersRequest } from '@src/types/offer';
import ListingPage from '@src/components/listing-page/ListingPage';
import { offer, product } from '@src/test-tools/data';

// test data
const offers = [offer];

// server mock
const server = setupServer(
  rest.post('/api/offers/search', async (req, res, ctx) => {
    const request = await req.json<SearchOffersRequest>();
    if (request.productCategory) {
      return res(
        ctx.json({
          offers: [
            {
              ...offer,
              price: '24.49',
              product: {
                ...product,
                name: 'Red Dress',
              },
            },
          ],
          total: offers.length,
        }),
      );
    }
    return res(
      ctx.json({
        offers: offers,
        total: offers.length,
      }),
    );
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());

describe('Listing Page', () => {
  it('should ListingPage component contain categories and list of offers', async () => {
    // given
    const listingPage = (
      <BrowserRouter>
        <ListingPage />
      </BrowserRouter>
    );

    // when
    const { getByRole, getAllByRole } = renderWithProviders(listingPage);

    // then wait until offer list is fetched
    await waitFor(() => {
      expect(getByRole('categoriesSidebar')).toBeInTheDocument();
      expect(getAllByRole('offerItem').length).toEqual(1);
    });
    expect(getByRole('productName').textContent).toEqual('Iphone');

    // and when user select specific category
    await act(() => {
      userEvent.click(getAllByRole('categoryLink')[0]);
    });

    // list of offers should be filtered
    await waitFor(() => {
      expect(getAllByRole('offerItem').length).toEqual(1);
      expect(getByRole('productName').textContent).toEqual('Red Dress');
    });
  });

  it('should display message when there are no offers in category', async () => {
    // given
    const listingPage = (
      <BrowserRouter>
        <ListingPage />
      </BrowserRouter>
    );

    server.use(
      rest.post('/api/offers/search', async (req, res, ctx) => {
        return res(
          ctx.json({
            offers: [],
            total: 0,
          }),
        );
      }),
    );

    // when
    const { getByRole } = renderWithProviders(listingPage);

    // then wait until offer list is fetched
    await waitFor(() => {
      expect(getByRole('categoriesSidebar')).toBeInTheDocument();
      expect(getByRole('empty-offer-message')).toBeInTheDocument();
      expect(getByRole('empty-offer-message').textContent).toEqual(
        'There are no offers in category',
      );
    });
  });
});
