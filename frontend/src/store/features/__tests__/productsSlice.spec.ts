import productsReducer, { addProducts } from './../productsSlice';
import { Product, ProductsState } from '@src/types/product';

describe('products reducer', () => {
  const initialState: ProductsState = { data: [], total: 0 };
  it('should handle initial state', () => {
    expect(productsReducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  it('should handle addProducts', () => {
    // given
    const productsPage = {
      products: [
        {
          id: '1',
          name: 'Iphone',
          ean: '123456',
          imageUrl: 'img.jpg',
          description: 'description 1',
        },
        {
          id: '2',
          name: 'IMac',
          ean: '432215',
          imageUrl: 'img.jpg',
          description: 'description 2',
        },
      ] as Product[],
      total: 2,
    };

    // when
    const actual = productsReducer(initialState, addProducts(productsPage));

    // then
    expect(actual).toEqual({ data: productsPage.products, total: productsPage.total });
  });
});
