import React from 'react';
import { createRoot } from 'react-dom/client';
import Application from './components/Application';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { setupStore } from '@src/store/store';
import 'owl.carousel/dist/assets/owl.carousel.css';
import 'owl.carousel/dist/assets/owl.theme.default.css';
import UserApiService from '@src/api/UserApiService';
import { addUser } from '@src/store/features/userSlice';

const store = setupStore({});

UserApiService.getUserCurrent()
  .then((response) => {
    store.dispatch(addUser(response.data));
  })
  .catch(() => {
    /** ignore */
  })
  .finally(() => {
    const app = (
      <Provider store={store}>
        <BrowserRouter>
          {' '}
          <Application />
        </BrowserRouter>
      </Provider>
    );
    createRoot(document.getElementById('app')).render(app);
  });
