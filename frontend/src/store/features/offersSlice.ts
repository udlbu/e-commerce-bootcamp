import { createSlice } from '@reduxjs/toolkit';
import { OffersState } from '@src/types/offer';

const initialState: OffersState = { data: [], total: 0 };

export const offersSlice = createSlice({
  name: 'offers',
  initialState,
  reducers: {
    addOffers: (state, action) => {
      state.data = action.payload.offers || [];
      state.total = action.payload.total || 0;
    },
  },
});

export const { addOffers } = offersSlice.actions;
export default offersSlice.reducer;
