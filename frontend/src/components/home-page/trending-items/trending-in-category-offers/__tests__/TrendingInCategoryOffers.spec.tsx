import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import TrendingInCategoryOffers from '@src/components/home-page/trending-items/trending-in-category-offers/TrendingInCategoryOffers';
import { offer } from '@src/test-tools/data';

const EXPECTED_NUMBER_OF_OFFERS_IN_CATEGORY = 3;
describe('TrendingInCategoryOffers', () => {
  it('should TrendingInCategoryOffers component be created', () => {
    // given
    const offers = [
      { ...offer, id: '1' },
      { ...offer, id: '2' },
      { ...offer, id: '3' },
    ];
    const trendingInCategoryOffers = (
      <BrowserRouter>
        <TrendingInCategoryOffers offers={offers} />
      </BrowserRouter>
    );

    // when
    const { getAllByRole } = renderWithProviders(trendingInCategoryOffers);

    // then
    expect(getAllByRole('trendingOffer').length).toEqual(EXPECTED_NUMBER_OF_OFFERS_IN_CATEGORY);
  });
});
