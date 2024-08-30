import { createSlice } from '@reduxjs/toolkit';
import { UserState } from '@src/types/user';

const initialState: UserState = null;

export const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    addUser: (state, action) => {
      return { ...action.payload };
    },
    logout: () => {
      return null;
    },
  },
});

export const { addUser, logout } = userSlice.actions;
export default userSlice.reducer;
