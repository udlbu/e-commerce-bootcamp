import React from 'react';

import { act, fireEvent, screen, waitFor } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import ProductAdminPage from '@src/components/product-admin-page/ProductAdminPage';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { AddProductRequest, ModifyProductRequest } from '@src/types/product';
import { product } from '@src/test-tools/data';
import '@src/test-tools/mocks';

const addProductBtn = 'button[role="addBtn"]';
const updateProductBtn = 'button[role="updateBtn"]';
const cancelProductBtn = 'button[role="cancelBtn"]';
const addProductsRequests = [] as AddProductRequest[];

const server = setupServer(
  rest.get('/api/products', (req, res, ctx) => {
    return res(
      ctx.json({
        products: [],
        total: 0,
      }),
    );
  }),
  rest.post('/api/products', (req, res, ctx) => {
    req.json().then((data) => {
      addProductsRequests.push(data);
    });
    return res(ctx.json([]));
  }),
);
beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());

describe('Product Admin Page', () => {
  it('should ProductAdminPage component be created', () => {
    // given
    const productAdminPage = (
      <BrowserRouter>
        <ProductAdminPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(productAdminPage);

    // then
    expect(getByRole('name')).toBeInTheDocument();
    expect(getByRole('ean')).toBeInTheDocument();
    expect(getByRole('category')).toBeInTheDocument();
    expect(getByRole('description')).toBeInTheDocument();
    expect(getByRole('image')).toBeInTheDocument();
    expect(getByRole('loaded-image')).toBeInTheDocument();
    expect(screen.queryByRole('loaded-image-url')).toBeNull();
    expect(getByRole('productsList')).toBeInTheDocument();
    expect(getByRole('addBtn')).toBeDisabled();
  });

  it('should add button be enabled when user enters required field', async () => {
    // given
    const productAdminPage = (
      <BrowserRouter>
        <ProductAdminPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(productAdminPage);
    fireEvent.change(getByRole('name'), { target: { value: 'IPhone 14 Pro' } });
    fireEvent.change(getByRole('ean'), { target: { value: '6743349632445' } });
    fireEvent.change(getByRole('category'), { target: { value: 'ELECTRONICS' } });

    // then add button should become enabled
    await waitFor(() => {
      expect(getByRole('addBtn')).toBeEnabled();
    });
  });

  it('should add product by calling rest API when AddBtn is clicked', async () => {
    // given
    const productAdminPage = (
      <BrowserRouter>
        <ProductAdminPage />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = renderWithProviders(productAdminPage);
    fireEvent.change(getByRole('name'), { target: { value: 'IPhone 14 Pro' } });
    fireEvent.change(getByRole('ean'), { target: { value: '6743349632445' } });
    fireEvent.change(getByRole('category'), { target: { value: 'ELECTRONICS' } });
    // and when product is added
    await act(async () => {
      await userEvent.click(container.querySelector(addProductBtn));
    });
    // then
    expect(addProductsRequests.length).toEqual(1);
    expect(addProductsRequests[0].name).toEqual('IPhone 14 Pro');
    expect(addProductsRequests[0].ean).toEqual('6743349632445');
    expect(addProductsRequests[0].category).toEqual('ELECTRONICS');

    // and field should be cleared
    expect(getByRole('name')).toHaveAttribute('value', '');
    expect(getByRole('ean')).toHaveAttribute('value', '');
  });

  it('should display error when a value in the fields is not valid', async () => {
    // given
    const productAdminPage = (
      <BrowserRouter>
        <ProductAdminPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(productAdminPage);
    fireEvent.change(getByRole('name'), { target: { value: ' ' } });
    fireEvent.change(getByRole('ean'), { target: { value: ' ' } });
    fireEvent.change(getByRole('category'), { target: { value: ' ' } });

    // then
    await waitFor(() => {
      expect(getByRole('nameError')).toHaveTextContent('Field is required');
      expect(getByRole('eanError')).toHaveTextContent('Field is required');
      expect(getByRole('categoryError')).toHaveTextContent('Field is required');
    });
  });

  it('should modify existing product', async () => {
    // given
    const products = [product];
    const modifyProductsRequests = [] as ModifyProductRequest[];
    server.use(
      rest.get('/api/products', (req, res, ctx) => {
        return res(
          ctx.json({
            products,
            total: products.length,
          }),
        );
      }),
      rest.put('/api/products', (req, res, ctx) => {
        req.json().then((data) => {
          modifyProductsRequests.push(data);
          products[0] = data;
        });
        return res(ctx.json([]));
      }),
    );

    const productAdminPage = (
      <BrowserRouter>
        <ProductAdminPage />
      </BrowserRouter>
    );

    // when
    const { container, getByRole, getByText } = renderWithProviders(productAdminPage);

    // wait for product to be fetched
    await waitFor(() => {
      expect(screen.queryByRole('productRow')).toBeInTheDocument();
    });

    // then click edit button
    await act(async () => {
      await userEvent.click(container.querySelector('a[role="editBtn"]'));
    });

    // and expect data to be loaded into fields
    expect(getByRole('name')).toHaveAttribute('value', 'Iphone');
    expect(getByRole('ean')).toHaveAttribute('value', '89712302');
    expect(getByRole('description')).toHaveAttribute('value', 'Iphone description');
    expect(getByRole('loaded-image-url')).toHaveAttribute('src', 'image.jpg');
    expect(screen.queryByRole('loaded-image')).toBeNull();
    expect(getByRole('updateBtn')).toBeInTheDocument();
    expect(getByRole('cancelBtn')).toBeInTheDocument();
    expect(screen.queryByRole('addBtn')).toBeNull();

    // then change some field value
    fireEvent.change(getByRole('name'), { target: { value: 'Samsung Galaxy' } });

    // when update button is clicked
    await act(async () => {
      await userEvent.click(container.querySelector(updateProductBtn));
    });

    // then fields are cleared
    expect(getByRole('name')).toHaveAttribute('value', '');
    expect(getByRole('ean')).toHaveAttribute('value', '');
    expect(getByRole('description')).toHaveAttribute('value', '');
    expect(getByRole('loaded-image')).toBeInTheDocument();
    expect(screen.queryByRole('loaded-image-url')).toBeNull();
    expect(screen.queryByRole('updateBtn')).toBeNull();
    expect(screen.queryByRole('cancelBtn')).toBeNull();
    expect(getByRole('addBtn')).toBeInTheDocument();

    // and modify request is handled
    expect(modifyProductsRequests.length).toEqual(1);
    expect(getByText('Samsung Galaxy')).toBeDefined();
  });

  it('should clear all field when user clicked "Cancel" in context of product modification', async () => {
    // given
    server.use(
      rest.get('/api/products', (req, res, ctx) => {
        return res(
          ctx.json({
            products: [product],
            total: 1,
          }),
        );
      }),
    );

    const productAdminPage = (
      <BrowserRouter>
        <ProductAdminPage />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = renderWithProviders(productAdminPage);

    // wait for product to be fetched
    await waitFor(() => {
      expect(screen.queryByRole('productRow')).toBeInTheDocument();
    });

    // then click edit button
    await act(async () => {
      await userEvent.click(container.querySelector('a[role="editBtn"]'));
    });

    // and expect data to be loaded into fields
    expect(getByRole('name')).toHaveAttribute('value', 'Iphone');
    expect(getByRole('ean')).toHaveAttribute('value', '89712302');
    expect(getByRole('description')).toHaveAttribute('value', 'Iphone description');
    expect(screen.queryByRole('loaded-image')).toBeNull();
    expect(getByRole('loaded-image-url')).toHaveAttribute('src', 'image.jpg');
    expect(getByRole('updateBtn')).toBeInTheDocument();
    expect(getByRole('cancelBtn')).toBeInTheDocument();
    expect(screen.queryByRole('addBtn')).toBeNull();

    // when cancel button is clicked
    await act(async () => {
      await userEvent.click(container.querySelector(cancelProductBtn));
    });

    // then fields are cleared
    expect(getByRole('name')).toHaveAttribute('value', '');
    expect(getByRole('ean')).toHaveAttribute('value', '');
    expect(getByRole('description')).toHaveAttribute('value', '');
    expect(getByRole('loaded-image')).toBeInTheDocument();
    expect(screen.queryByRole('loaded-image-url')).toBeNull();
    expect(screen.queryByRole('updateBtn')).toBeNull();
    expect(screen.queryByRole('cancelBtn')).toBeNull();
    expect(getByRole('addBtn')).toBeInTheDocument();
  });
});
