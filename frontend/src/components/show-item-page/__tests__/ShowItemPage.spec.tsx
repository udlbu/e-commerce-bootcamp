import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import ShowItemPage from '@src/components/show-item-page/ShowItemPage';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import { offer } from '@src/test-tools/data';
import { screen, waitFor } from '@testing-library/react';

// server mock
const server = setupServer(
  rest.get('/api/offers/:id', (req, res, ctx) => {
    return res(ctx.json(offer));
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());

// mock router useParams function
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useParams: () => ({
    id: '1',
  }),
}));

describe('ShowItemPage', () => {
  it('should ShowItemPage component be created', async () => {
    // given
    const showItemPage = (
      <BrowserRouter>
        <ShowItemPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(showItemPage);

    // then when offer is still fetching
    expect(getByRole('preloader')).toBeInTheDocument();
    expect(screen.queryByRole('showItemPage')).toBeNull();

    // and then
    await waitFor(() => {
      expect(screen.queryByRole('preloader')).toBeNull();
      expect(getByRole('showItemPage')).toBeInTheDocument();
    });
  });
});
