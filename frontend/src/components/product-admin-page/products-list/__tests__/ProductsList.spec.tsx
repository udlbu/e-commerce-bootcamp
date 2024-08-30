import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { userEvent } from '@testing-library/user-event';
import { act, screen, waitFor } from '@testing-library/react';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import ProductsList from '@src/components/product-admin-page/products-list/ProductsList';
import { product } from '@src/test-tools/data';

const editButton = 'a[role="editBtn"]';
const deleteButton = 'a[role="deleteBtn"]';

const PRODUCT_ID = '1';

const products = [product];
const deleteProductRequests = [] as string[];
const server = setupServer(
  rest.get('/api/products', (req, res, ctx) => {
    return res(
      ctx.json({
        products,
        total: products.length,
      }),
    );
  }),

  rest.delete('/api/products/:id', (req, res, ctx) => {
    products.splice(0, products.length);
    deleteProductRequests.push(req.params.id as string);
    return res(ctx.json({}));
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
  if (products.length == 0) {
    products.push(product);
  }
});
afterAll(() => server.close());

describe('ProductsList', () => {
  it('should ProductsList component be created with "empty header" when there are no products', async () => {
    // given
    const onEnterEditingFn = jest.fn();
    const productsList = (
      <BrowserRouter>
        <ProductsList onEnterEditing={onEnterEditingFn} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(productsList);

    // then
    expect(getByRole('imageHeader')).toBeInTheDocument();
    expect(getByRole('nameHeader')).toBeInTheDocument();
    expect(getByRole('descriptionHeader')).toBeInTheDocument();
    expect(getByRole('categoryHeader')).toBeInTheDocument();
    expect(getByRole('eanHeader')).toBeInTheDocument();
    expect(getByRole('deleteHeader')).toBeInTheDocument();
    expect(getByRole('editHeader')).toBeInTheDocument();

    // and table is empty
    expect(getByRole('emptyRow')).toBeInTheDocument();
    expect(screen.queryByRole('productRow')).toBeNull();

    // and after products list is fetched then
    await waitFor(() => {
      expect(screen.queryByRole('emptyRow')).toBeNull();
      expect(getByRole('productRow')).toBeInTheDocument();
      expect(getByRole('imageColumn')).toBeInTheDocument();
      expect(getByRole('nameColumn')).toBeInTheDocument();
      expect(getByRole('eanColumn')).toBeInTheDocument();
      expect(getByRole('categoryColumn')).toBeInTheDocument();
      expect(getByRole('descriptionColumn')).toBeInTheDocument();
      expect(getByRole('deleteBtn')).toBeInTheDocument();
      expect(getByRole('editBtn')).toBeInTheDocument();
    });
  });

  it('should call "onEnterEditing" when user clicked "Edit button"', async () => {
    // given
    const onEnterEditingFn = jest.fn();
    const productsList = (
      <BrowserRouter>
        <ProductsList onEnterEditing={onEnterEditingFn} />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(productsList, {
      preloadedState: {
        products: {
          data: [product],
          total: 1,
        },
      },
    });

    // and
    await act(async () => {
      await userEvent.click(container.querySelector(editButton));
    });

    // then
    expect(onEnterEditingFn).toBeCalledTimes(1);
    expect(onEnterEditingFn).toBeCalledWith(product);
  });

  it('should delete product when "Delete" button is clicked', async () => {
    // given
    const onEnterEditingFn = jest.fn();
    const productsList = (
      <BrowserRouter>
        <ProductsList onEnterEditing={onEnterEditingFn} />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = renderWithProviders(productsList);

    // then
    expect(getByRole('emptyRow')).toBeInTheDocument();
    expect(screen.queryByRole('productRow')).toBeNull();

    // wait until products are fetched
    await waitFor(() => {
      expect(getByRole('productRow')).toBeInTheDocument();
      expect(screen.queryByRole('emptyRow')).toBeNull();
    });

    // then delete first product
    await act(async () => {
      await userEvent.click(container.querySelector(deleteButton));
    });

    // then wait until table is empty again
    await waitFor(() => {
      expect(getByRole('emptyRow')).toBeInTheDocument();
      expect(screen.queryByRole('productRow')).toBeNull();
    });

    expect(deleteProductRequests.length).toEqual(1);
    expect(deleteProductRequests[0]).toEqual(PRODUCT_ID);
  });
});
