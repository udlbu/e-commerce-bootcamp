import cartReducer, { addCart, clearCart } from './../cartSlice';
import { CartState } from '@src/types/cart';
import { ProductCategory } from '@src/types/product';

describe('cart reducer', () => {
  const initialState: CartState = null;
  it('should handle initial state', () => {
    expect(cartReducer(undefined, { type: 'unknown' })).toEqual(null);
  });

  it('should handle addCart and clearCart', () => {
    // given
    const cart = {
      id: '1',
      userId: '1',
      items: [
        {
          id: '1',
          offerId: '1',
          productName: 'product-name',
          ean: '111222333',
          imageUrl: 'image.jpg',
          price: '11.99',
          quantity: 1,
          category: ProductCategory.CLOTHING,
          description: 'description',
        },
      ],
    } as CartState;

    // when
    const actual = cartReducer(initialState, addCart(cart));

    // then
    expect(actual).toEqual(cart);

    // and when
    const cleared = cartReducer(initialState, clearCart());

    // then
    expect(cleared).toEqual(null);
  });

  it('should handle addCart with empty cart', () => {
    // given
    const cart = {
      id: null,
      userId: null,
      items: [],
    } as CartState;

    // when
    const actual = cartReducer(initialState, addCart(cart));

    // then
    expect(actual).toEqual(initialState);
  });
});
