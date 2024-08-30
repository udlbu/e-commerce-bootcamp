import offersReducer, { addOffers } from './../offersSlice';
import { Offer, OffersState } from '@src/types/offer';
import { ProductCategory } from '@src/types/product';

describe('offers reducer', () => {
  const initialState: OffersState = { data: [], total: 0 };
  it('should handle initial state', () => {
    expect(offersReducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  it('should handle addOffers', () => {
    // given
    const offerPage = {
      offers: [
        {
          id: '1',
          userId: '1',
          price: '9.99',
          quantity: '1',
          status: 'ACTIVE',
          version: 1,
          product: {
            id: '1',
            name: 'Iphone',
            ean: '123456',
            imageUrl: 'img.jpg',
            category: ProductCategory.ELECTRONICS,
            description: 'description 1',
            version: 1,
          },
        },
      ] as Offer[],
      total: 1,
    };

    // when
    const actual = offersReducer(initialState, addOffers(offerPage));

    // then
    expect(actual).toEqual({ data: offerPage.offers, total: offerPage.total });
  });
});
