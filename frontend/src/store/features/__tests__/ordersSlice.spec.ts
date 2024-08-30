import ordersReducer, { addOrders } from './../ordersSlice';
import { DeliveryMethod, OrderLine, OrdersState, PaymentMethod } from '@src/types/order';

describe('orders reducer', () => {
  const initialState: OrdersState = { data: [], total: 0 };
  it('should handle initial state', () => {
    expect(ordersReducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  it('should handle addOrders', () => {
    // given
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
              id: 1,
              offerId: '1',
              offerPrice: 9.99,
              productId: '1',
              productName: 'Iphone 15 Pro 1GB',
              productEan: '123456789',
              productCategory: 'ELECTRONICS',
              imageUrl: 'img.jpg',
              quantity: 1,
              version: 1,
            },
          ] as OrderLine[],
        },
      ],
      total: 1,
    };

    // when
    const actual = ordersReducer(initialState, addOrders(orderPage));

    // then
    expect(actual).toEqual({ data: orderPage.orders, total: orderPage.total });
  });
});
