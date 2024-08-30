import { createSlice } from '@reduxjs/toolkit';
import { OfferDetailsState } from '@src/types/offer';

const initialState: OfferDetailsState = { offer: null };

export const offerDetailsSlice = createSlice({
  name: 'offerDetails',
  initialState,
  reducers: {
    addOffer: (state, action) => {
      state.offer = action.payload;
    },
  },
});

export const { addOffer } = offerDetailsSlice.actions;
export default offerDetailsSlice.reducer;
