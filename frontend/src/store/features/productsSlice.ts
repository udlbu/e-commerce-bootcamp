import { createSlice } from '@reduxjs/toolkit';
import { ProductsState } from '@src/types/product';

const initialState: ProductsState = { data: [], total: 0 };

export const productsSlice = createSlice({
  name: 'products',
  initialState,
  reducers: {
    addProducts: (state, action) => {
      state.data = action.payload.products || [];
      state.total = action.payload.total || [];
    },
  },
});

export const { addProducts } = productsSlice.actions;
export default productsSlice.reducer;
