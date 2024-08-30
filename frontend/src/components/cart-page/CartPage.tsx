import React, { useState } from 'react';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';
import CartSummary from '@src/components/cart-page/cart-summary/CartSummary';
import CartItemRow from '@src/components/cart-page/cart-item-row/CartItemRow';
import CartHeader from '@src/components/cart-page/cart-header/CartHeader';
import { cartPageBreadcrumb } from '@src/domain/breadcrumbs';
import { useAppSelector } from '@src/store/hooks';
import OrderThankYouPage from '@src/components/order-thank-you-page/OrderThankYouPage';

const CartPage: React.FC = () => {
  const cart = useAppSelector((state) => state.cart);
  const [submitted, setSubmitted] = useState(false);
  return (
    <>
      <Breadcrumbs elements={cartPageBreadcrumb} />
      {!submitted && (
        <div className='shopping-cart section'>
          {cart?.items?.length > 0 && (
            <div className='container'>
              <div className='row'>
                <div className='col-12'>
                  <table className='table table-content'>
                    <thead>
                      <CartHeader />
                    </thead>
                    <tbody>
                      {cart?.items?.map((item) => (
                        <CartItemRow key={item.id} item={item} />
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
              <CartSummary onOrderSubmitSuccess={() => setSubmitted(true)} />
            </div>
          )}
          {!(cart?.items?.length > 0) && (
            <div role='empty-cart' className='container'>
              Your cart is empty
            </div>
          )}
        </div>
      )}
      {submitted && <OrderThankYouPage />}
    </>
  );
};

export default CartPage;
