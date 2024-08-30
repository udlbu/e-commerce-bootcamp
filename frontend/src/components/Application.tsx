import React from 'react';
import './Application.module.scss';
import '../../styles/style.scss';
import '../../styles/form.scss';
import Header from '@src/components/shared/header/Header';
import Footer from '@src/components/shared/footer/Footer';
import HomePage from '@src/components/home-page/HomePage';
import { Route, Routes } from 'react-router-dom';
import CartPage from '@src/components/cart-page/CartPage';
import ListingPage from '@src/components/listing-page/ListingPage';
import LoginPage from '@src/components/login-page/LoginPage';
import ShowItemPage from '@src/components/show-item-page/ShowItemPage';
import RegisterPage from '@src/components/register-page/RegisterPage';
import ProductAdminPage from '@src/components/product-admin-page/ProductAdminPage';
import OfferAdminPage from '@src/components/offer-admin-page/OfferAdminPage';
import AuthWrapperRoute, {
  AccessType,
} from '@src/components/shared/auth-wrapper-route/AuthWrapperRoute';
import MyOrdersPage from '@src/components/my-orders-page/MyOrdersPage';
import EditUserPage from '@src/components/edit-user-page/EditUserPage';
import ChangePasswordPage from '@src/components/change-password-page/ChangePasswordPage';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import NotFoundPage from '@src/components/not-found-page/NotFoundPage';
import ActivatedPage from '@src/components/activated-page/ActivatedPage';

const Application: React.FC = () => {
  return (
    <>
      <Header />
      <ToastContainer />
      <Routes>
        <Route index path='/' element={<HomePage />} />
        <Route index path='/activated' element={<ActivatedPage />} />
        <Route
          path='cart'
          element={
            <AuthWrapperRoute accessType={AccessType.AUTH_REQUIRED}>
              <CartPage />
            </AuthWrapperRoute>
          }
        />
        <Route path='listing' element={<ListingPage />} />
        <Route
          path='login'
          element={
            <AuthWrapperRoute accessType={AccessType.AUTH_NOT_ALLOWED}>
              <LoginPage />
            </AuthWrapperRoute>
          }
        />
        <Route path='offer/:id' element={<ShowItemPage />} />
        <Route
          path='register'
          element={
            <AuthWrapperRoute accessType={AccessType.AUTH_NOT_ALLOWED}>
              <RegisterPage />
            </AuthWrapperRoute>
          }
        />
        <Route
          path='products'
          element={
            <AuthWrapperRoute accessType={AccessType.AUTH_REQUIRED}>
              <ProductAdminPage />
            </AuthWrapperRoute>
          }
        />
        <Route
          path='offers'
          element={
            <AuthWrapperRoute accessType={AccessType.AUTH_REQUIRED}>
              <OfferAdminPage />
            </AuthWrapperRoute>
          }
        />
        <Route
          path='orders'
          element={
            <AuthWrapperRoute accessType={AccessType.AUTH_REQUIRED}>
              <MyOrdersPage />
            </AuthWrapperRoute>
          }
        />
        <Route
          path='user'
          element={
            <AuthWrapperRoute accessType={AccessType.AUTH_REQUIRED}>
              <EditUserPage />
            </AuthWrapperRoute>
          }
        />
        <Route
          path='change-password'
          element={
            <AuthWrapperRoute accessType={AccessType.AUTH_REQUIRED}>
              <ChangePasswordPage />
            </AuthWrapperRoute>
          }
        />
        <Route path='*' element={<NotFoundPage />} />
      </Routes>
      <Footer />
    </>
  );
};

export default Application;
