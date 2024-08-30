import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import TrendingItems from '@src/components/home-page/trending-items/TrendingItems';
import { ProductCategory } from '@src/types/product';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import { SearchOffersRequest } from '@src/types/offer';
import { offer, product } from '@src/test-tools/data';
import { act, waitFor } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';

const EXPECTED_NUMBER_OF_OFFERS_TRENDING_IN_ELECTRONICS_CATEGORY = 4;
const EXPECTED_NUMBER_OF_OFFERS_TRENDING_IN_CLOTHING_CATEGORY = 2;

// server mock
const server = setupServer(
  rest.post('/api/offers/search', async (req, res, ctx) => {
    const request = await req.json<SearchOffersRequest>();
    if (request.productCategory === 'ELECTRONICS') {
      return res(
        ctx.json({
          offers: [
            {
              ...offer,
              id: '1',
            },
            {
              ...offer,
              id: '2',
            },
            {
              ...offer,
              id: '3',
            },
            {
              ...offer,
              id: '4',
            },
          ],
          total: 4,
        }),
      );
    }
    if (request.productCategory === 'CLOTHING') {
      return res(
        ctx.json({
          offers: [
            {
              ...offer,
              product: {
                ...product,
                category: ProductCategory.CLOTHING,
              },
              id: '5',
            },
            {
              ...offer,
              product: {
                ...product,
                category: ProductCategory.CLOTHING,
              },
              id: '6',
            },
          ],
          total: 2,
        }),
      );
    }
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());
describe('TrendingItems', () => {
  it('should TrendingItems component be created', async () => {
    // given
    const trendingItems = (
      <BrowserRouter>
        <TrendingItems />
      </BrowserRouter>
    );

    // when
    const { getAllByRole, getByRole } = renderWithProviders(trendingItems);

    // then
    expect(getByRole('trendingTitle')).toBeInTheDocument();
    expect(getAllByRole('categoryTab').length).toEqual(Object.keys(ProductCategory).length);

    // and first category should be active
    expect(getAllByRole('tabLink')[0]).toHaveClass('active');

    // and should have 4 offers in category
    await waitFor(() => {
      expect(getAllByRole('trendingOffer').length).toEqual(
        EXPECTED_NUMBER_OF_OFFERS_TRENDING_IN_ELECTRONICS_CATEGORY,
      );
    });
  });

  it('should change Category header and display offers from selected category when other category label is clicked by user', async () => {
    // given
    const trendingItems = (
      <BrowserRouter>
        <TrendingItems />
      </BrowserRouter>
    );

    // when
    const { getAllByRole } = renderWithProviders(trendingItems);
    const clothingCategory = getAllByRole('tabLink')[1];

    // then
    expect(clothingCategory.textContent).toEqual('Clothing');
    expect(clothingCategory).not.toHaveClass('active');

    // and when
    act(() => {
      userEvent.click(clothingCategory);
    });

    // then
    await waitFor(() => {
      expect(clothingCategory).toHaveClass('active');
      expect(getAllByRole('trendingOffer').length).toEqual(
        EXPECTED_NUMBER_OF_OFFERS_TRENDING_IN_CLOTHING_CATEGORY,
      );
    });
  });
});
