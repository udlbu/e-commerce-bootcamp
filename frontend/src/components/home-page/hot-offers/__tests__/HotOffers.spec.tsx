import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import { offer } from '@src/test-tools/data';
import HotOffers from '@src/components/home-page/hot-offers/HotOffers';
import { waitFor } from '@testing-library/react';
import { setupServer } from 'msw/node';
import { rest } from 'msw';

// server mock
const server = setupServer(
  rest.post('/api/offers/search', async (req, res, ctx) => {
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
  }),
);

beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());
describe('HotOffers', () => {
  it('should HotOffers component be created', async () => {
    // given
    const hotOffers = (
      <BrowserRouter>
        <HotOffers />
      </BrowserRouter>
    );

    // when
    const { getAllByRole, getByRole } = renderWithProviders(hotOffers, {
      preloadedState: {
        offers: { data: [{ ...offer }], total: 1 },
      },
    });

    // then
    expect(getByRole('hot-offers-role').textContent).toEqual('Hot Items');
    await waitFor(() => {
      expect(getAllByRole('single-product').length).toEqual(4);
    });
  });
});
