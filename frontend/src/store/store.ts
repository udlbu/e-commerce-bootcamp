import {
  // eslint-disable-next-line import/named
  Action,
  combineReducers,
  configureStore,
  // eslint-disable-next-line import/named
  PreloadedState,
  // eslint-disable-next-line import/named
  ThunkAction,
} from '@reduxjs/toolkit';
import userReducer from './features/userSlice';
import productsReducer from './features/productsSlice';
import offersReducer from './features/offersSlice';
import listingOffersReducer from './features/listingOffersSlice';
import offerDetailsReducer from './features/offerDetailsSlice';
import cartReducer from './features/cartSlice';
import ordersReducer from './features/ordersSlice';

export const rootReducer = combineReducers({
  user: userReducer,
  products: productsReducer,
  offers: offersReducer,
  listingOffers: listingOffersReducer,
  offerDetails: offerDetailsReducer,
  cart: cartReducer,
  orders: ordersReducer,
});

export function setupStore(preloadedState?: PreloadedState<RootState>) {
  return configureStore({
    reducer: rootReducer,
    preloadedState,
  });
}

export type AppDispatch = AppStore['dispatch'];
export type RootState = ReturnType<typeof rootReducer>;
export type AppThunk<ReturnType = void> = ThunkAction<
  ReturnType,
  RootState,
  unknown,
  Action<string>
>;
export type AppStore = ReturnType<typeof setupStore>;
