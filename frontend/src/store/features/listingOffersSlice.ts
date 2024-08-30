import { createSlice } from '@reduxjs/toolkit';
import { OffersState } from '@src/types/offer';

const initialState: OffersState = { data: [], total: 0 };

export const listingOffersSlice = createSlice({
  name: 'listingOffers',
  initialState,
  reducers: {
    addListingOffers: (state, action) => {
      state.data = action.payload.offers || [];
      state.total = action.payload.total || 0;
    },
  },
});

export const { addListingOffers } = listingOffersSlice.actions;
export default listingOffersSlice.reducer;
