import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { userEvent } from '@testing-library/user-event';
import { act, screen, waitFor } from '@testing-library/react';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import OffersList from '@src/components/offer-admin-page/offers-list/OffersList';
import { offer, user } from '@src/test-tools/data';

const editButton = 'a[role="editBtn"]';
const deleteButton = 'a[role="deleteBtn"]';
const changeStatusButton = 'button[role="changeStatusBtn"]';

const OFFER_ID = '1';

const offers = [offer];
const deleteOfferRequests = [] as string[];
const server = setupServer(
  rest.get('/api/offers/user/search', (req, res, ctx) => {
    return res(
      ctx.json({
        offers,
        total: offers.length,
      }),
    );
  }),

  rest.delete('/api/offers/:id', (req, res, ctx) => {
    offers.splice(0, offers.length);
    deleteOfferRequests.push(req.params.id as string);
    return res(ctx.json({}));
  }),

  rest.put('/api/offers/:id/activate', (req, res, ctx) => {
    offers[0] = {
      ...offer,
      status: 'ACTIVE',
    };
    return res(ctx.json({}));
  }),

  rest.put('/api/offers/:id/deactivate', (req, res, ctx) => {
    offers[0] = {
      ...offer,
      status: 'INACTIVE',
    };
    return res(ctx.json({}));
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
  if (offers.length == 0) {
    offers.push(offer);
  }
});
afterAll(() => server.close());

describe('OffersList', () => {
  it('should OffersList component be created with "empty header" when there are no offers', async () => {
    // given
    const onEnterEditingFn = jest.fn();
    const offersList = (
      <BrowserRouter>
        <OffersList onEnterEditing={onEnterEditingFn} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(offersList, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('idHeader')).toBeInTheDocument();
    expect(getByRole('productNameHeader')).toBeInTheDocument();
    expect(getByRole('productCategoryHeader')).toBeInTheDocument();
    expect(getByRole('priceHeader')).toBeInTheDocument();
    expect(getByRole('quantityHeader')).toBeInTheDocument();
    expect(getByRole('statusHeader')).toBeInTheDocument();
    expect(getByRole('deleteHeader')).toBeInTheDocument();
    expect(getByRole('editHeader')).toBeInTheDocument();
    expect(getByRole('actionHeader')).toBeInTheDocument();

    // and table is empty
    expect(getByRole('emptyRow')).toBeInTheDocument();
    expect(screen.queryByRole('offerRow')).toBeNull();

    // and after offers list is fetched then
    await waitFor(() => {
      expect(screen.queryByRole('emptyRow')).toBeNull();
      expect(getByRole('offerRow')).toBeInTheDocument();
      expect(getByRole('idColumn')).toBeInTheDocument();
      expect(getByRole('nameColumn')).toBeInTheDocument();
      expect(getByRole('categoryColumn')).toBeInTheDocument();
      expect(getByRole('priceColumn')).toBeInTheDocument();
      expect(getByRole('quantityColumn')).toBeInTheDocument();
      expect(getByRole('statusColumn')).toBeInTheDocument();
      expect(getByRole('deleteBtn')).toBeInTheDocument();
      expect(getByRole('editBtn')).toBeInTheDocument();
      expect(getByRole('changeStatusBtn')).toBeInTheDocument();
    });
  });

  it('should call "onEnterEditing" when user clicked "Edit button"', async () => {
    // given
    const onEnterEditingFn = jest.fn();
    const offersList = (
      <BrowserRouter>
        <OffersList onEnterEditing={onEnterEditingFn} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(offersList, {
      preloadedState: {
        offers: {
          data: offers,
          total: offers.length,
        },
        user,
      },
    });

    // and
    await act(async () => {
      await userEvent.click(container.querySelector(editButton));
    });

    // then
    expect(onEnterEditingFn).toBeCalledTimes(1);
    expect(onEnterEditingFn).toBeCalledWith(offer);
  });

  it('should delete offer when "Delete" button is clicked', async () => {
    // given
    const onEnterEditingFn = jest.fn();
    const offersList = (
      <BrowserRouter>
        <OffersList onEnterEditing={onEnterEditingFn} />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = renderWithProviders(offersList, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('emptyRow')).toBeInTheDocument();
    expect(screen.queryByRole('offerRow')).toBeNull();

    // wait until products are fetched
    await waitFor(() => {
      expect(getByRole('offerRow')).toBeInTheDocument();
      expect(screen.queryByRole('emptyRow')).toBeNull();
    });

    // then delete first product
    await act(async () => {
      await userEvent.click(container.querySelector(deleteButton));
    });

    // then wait until table is empty again
    await waitFor(() => {
      expect(getByRole('emptyRow')).toBeInTheDocument();
      expect(screen.queryByRole('offerRow')).toBeNull();
    });

    expect(deleteOfferRequests.length).toEqual(1);
    expect(deleteOfferRequests[0]).toEqual(OFFER_ID);
  });

  it('should activate an inactive offer and deactivate an active one', async () => {
    // given
    const onEnterEditingFn = jest.fn();
    const offersList = (
      <BrowserRouter>
        <OffersList onEnterEditing={onEnterEditingFn} />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = renderWithProviders(offersList, {
      preloadedState: {
        offers: {
          data: offers,
          total: offers.length,
        },
        user,
      },
    });

    // then offer should be inactive at the beginning
    expect(getByRole('statusColumn').textContent).toEqual('INACTIVE');
    expect(getByRole('changeStatusBtn').textContent).toEqual('Activate');

    // then activate offer
    await act(async () => {
      await userEvent.click(container.querySelector(changeStatusButton));
    });

    // and then offer should become active
    await waitFor(() => {
      expect(getByRole('statusColumn').textContent).toEqual('ACTIVE');
      expect(getByRole('changeStatusBtn').textContent).toEqual('Deactivate');
    });

    // then deactivate offer again
    await act(async () => {
      await userEvent.click(container.querySelector(changeStatusButton));
    });

    // should become inactive again
    await waitFor(() => {
      expect(getByRole('statusColumn').textContent).toEqual('INACTIVE');
      expect(getByRole('changeStatusBtn').textContent).toEqual('Activate');
    });
  });
});
