import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import { screen, waitFor } from '@testing-library/react';
import { setupServer } from 'msw/node';
import { rest } from 'msw';
import MyOrdersPage from '@src/components/my-orders-page/MyOrdersPage';
import { user } from '@src/test-tools/data';
import { DeliveryMethod, PaymentMethod } from '@src/types/order';

const orderPage = {
  orders: [
    {
      id: '1',
      deliveryMethod: DeliveryMethod[DeliveryMethod.DHL],
      deliveryStatus: 'DELIVERED',
      paymentMethod: PaymentMethod[PaymentMethod.CARD],
      paymentStatus: 'SUCCESS',
      status: 'NEW',
      createdAt: '2023-10-03T19:38:34.127464Z',
      updatedAt: '2023-10-03T19:38:34.127464Z',
      version: 1,
      lines: [
        {
          id: '1',
          offerId: '1',
          offerPrice: 50.9,
          productId: '1',
          productName: 'Iphone 15 Pro 1GB',
          productEan: '123456789',
          productCategory: 'ELECTRONICS',
          imageUrl: 'img_1.jpg',
          quantity: 1,
          version: 1,
        },
        {
          id: '2',
          offerId: '2',
          offerPrice: 100,
          productId: '2',
          productName: 'Samsung G15',
          productEan: '654322122',
          productCategory: 'ELECTRONICS',
          imageUrl: 'img_2.jpg',
          quantity: 2,
          version: 1,
        },
      ],
    },
  ],
  total: 1,
};
const server = setupServer(
  rest.post('/api/orders/search', (req, res, ctx) => {
    return req.json().then(() => {
      return res(ctx.json(orderPage));
    });
  }),
);

beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => server.close());
describe('MyOrdersPage', () => {
  it('should MyOrdersPage component be created', async () => {
    // given
    const myOrdersPage = (
      <BrowserRouter>
        <MyOrdersPage />
      </BrowserRouter>
    );

    // when
    const { getByRole, getAllByRole } = renderWithProviders(myOrdersPage, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    await waitFor(() => {
      expect(getByRole('orders-table')).toBeInTheDocument();
      expect(screen.queryByRole('empty-orders-table')).toBeNull();

      expect(getByRole('order-id').textContent).toEqual('Order Id:\xa01');
      expect(getByRole('order-value').textContent).toEqual('Order value:\xa0$250.90');
      expect(getByRole('payment-method').textContent).toEqual('Payment method:\xa0CARD');
      expect(getByRole('delivery-status').textContent).toEqual('Delivery status:\xa0DELIVERED');
      expect(getByRole('product-header').textContent).toEqual('PRODUCT');
      expect(getByRole('name-header').textContent).toEqual('NAME');
      expect(getByRole('unit-price-header').textContent).toEqual('UNIT PRICE');
      expect(getByRole('quantity-header').textContent).toEqual('QUANTITY');
      expect(getByRole('total-header').textContent).toEqual('TOTAL');
      expect(getAllByRole('product-img').length).toEqual(2);
      expect(getAllByRole('product-img')[0]).toHaveAttribute('src', 'img_1.jpg');
      expect(getAllByRole('product-img')[1]).toHaveAttribute('src', 'img_2.jpg');
      expect(getAllByRole('product-name').length).toEqual(2);
      expect(getAllByRole('product-name')[0].textContent).toEqual('Iphone 15 Pro 1GB');
      expect(getAllByRole('product-name')[1].textContent).toEqual('Samsung G15');
      expect(getAllByRole('price').length).toEqual(2);
      expect(getAllByRole('price')[0].textContent).toEqual('$50.90');
      expect(getAllByRole('price')[1].textContent).toEqual('$100.00');
      expect(getAllByRole('quantity').length).toEqual(2);
      expect(getAllByRole('quantity')[0].textContent).toEqual('1');
      expect(getAllByRole('quantity')[1].textContent).toEqual('2');
      expect(getAllByRole('total-amount').length).toEqual(2);
      expect(getAllByRole('total-amount')[0].textContent).toEqual('$50.90');
      expect(getAllByRole('total-amount')[1].textContent).toEqual('$200.00');
    });
  });

  it('should MyOrdersPage with empty orders list be created', async () => {
    // given
    server.use(
      rest.post('/api/orders/search', (req, res, ctx) => {
        return req.json().then(() => {
          return res(ctx.json({ orders: [], total: 0 }));
        });
      }),
    );
    const myOrdersPage = (
      <BrowserRouter>
        <MyOrdersPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(myOrdersPage, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    await waitFor(() => {
      expect(getByRole('empty-orders-table')).toBeInTheDocument();
      expect(screen.queryByRole('orders-table')).toBeNull();
    });
  });
});
