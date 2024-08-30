import { CartItem, countCartItems } from '@src/types/cart';
import { cartItem } from '@src/test-tools/data';

describe('cart calculations', () => {
  [
    {
      id: '1',
      userId: '1',
      offerId: '1',
      items: [] as CartItem[],
    },
    {
      id: '1',
      userId: '1',
      offerId: '1',
      items: null,
    },
    {
      id: '1',
      userId: '1',
      offerId: '1',
      items: undefined,
    },
  ].forEach((cart) => {
    it('should count of cart items be equals to 0', () => {
      expect(countCartItems(cart)).toEqual(0);
    });
  });

  [
    {
      cart: {
        id: '1',
        userId: '1',
        offerId: '1',
        items: [cartItem],
      },
      expected: 1,
    },
    {
      cart: {
        id: '1',
        userId: '1',
        offerId: '1',
        items: [
          { ...cartItem, quantity: 1 },
          { ...cartItem, quantity: 1 },
          { ...cartItem, quantity: 1 },
        ],
      },
      expected: 3,
    },
    {
      cart: {
        id: '1',
        userId: '1',
        offerId: '1',
        items: [
          { ...cartItem, quantity: 2 },
          { ...cartItem, quantity: 3 },
          { ...cartItem, quantity: 5 },
        ],
      },
      expected: 10,
    },
  ].forEach((test) => {
    it('should count cart items', () => {
      expect(countCartItems(test.cart)).toEqual(test.expected);
    });
  });
});
