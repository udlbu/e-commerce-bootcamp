import { createSlice } from '@reduxjs/toolkit';
import { OrdersState } from '@src/types/order';

const initialState: OrdersState = { data: [], total: 0 };

export const ordersSlice = createSlice({
  name: 'orders',
  initialState,
  reducers: {
    addOrders: (state, action) => {
      state.data = action.payload.orders || [];
      state.total = action.payload.total || 0;
    },
  },
});

export const { addOrders } = ordersSlice.actions;
export default ordersSlice.reducer;
