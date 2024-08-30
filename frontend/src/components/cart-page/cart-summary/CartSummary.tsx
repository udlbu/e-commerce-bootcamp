import React from 'react';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { totalCartValue } from '@src/types/cart';
import { Price } from '@src/components/shared/price/Price';
import OrderApiService from '@src/api/OrderApiService';
import { DeliveryMethod, PaymentMethod } from '@src/types/order';
import { clearCart } from '@src/store/features/cartSlice';
import { handleError } from '@src/error-handler/error-handler';
import { useNavigate } from 'react-router-dom';

interface CartSummaryProps {
  onOrderSubmitSuccess: () => void;
}
const CartSummary: React.FC<CartSummaryProps> = ({ onOrderSubmitSuccess }) => {
  const cart = useAppSelector((state) => state.cart);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const handleSubmitOrder = async () => {
    try {
      await OrderApiService.submitOrder({
        cartId: cart.id,
        deliveryMethod: DeliveryMethod[DeliveryMethod.DHL],
        paymentMethod: PaymentMethod[PaymentMethod.TRANSFER],
      });
      onOrderSubmitSuccess();
      dispatch(clearCart());
    } catch (ex) {
      handleError(ex);
    }
  };
  return (
    <div className='row'>
      <div className='col-12'>
        <div className='total-amount'>
          <div className='row'>
            <div className='col-lg-8 col-md-5 col-12'>&nbsp;</div>
            <div className='col-lg-4 col-md-7 col-12'>
              <div className='right'>
                <ul>
                  <li>
                    Cart Subtotal
                    <span role='total-net-amount'>
                      <Price value={totalCartValue(cart)} />
                    </span>
                  </li>
                  <li>
                    Shipping<span>Free</span>
                  </li>
                  <li>
                    You Save<span>$20.00</span>
                  </li>
                  <li className='last'>
                    You Pay
                    <span role='total-gross-amount'>
                      <Price value={totalCartValue(cart)} />
                    </span>
                  </li>
                </ul>
                <div className='button5'>
                  <a role='submit-order-btn' className='btn' onClick={handleSubmitOrder}>
                    Submit order
                  </a>
                  <a
                    role='continue-shopping-btn'
                    className='btn'
                    onClick={() => navigate('/listing')}
                  >
                    Continue shopping
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CartSummary;
