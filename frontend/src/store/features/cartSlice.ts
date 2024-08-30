import { createSlice } from '@reduxjs/toolkit';
import { CartState } from '@src/types/cart';

const initialState: CartState = null;
export const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addCart: (state, action) => {
      if (!(action.payload as CartState).id) {
        return initialState;
      }
      return { ...action.payload };
    },
    clearCart: () => {
      return initialState;
    },
  },
});

export const { addCart, clearCart } = cartSlice.actions;
export default cartSlice.reducer;
