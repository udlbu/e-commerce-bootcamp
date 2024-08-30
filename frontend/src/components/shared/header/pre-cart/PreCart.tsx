import React, { useEffect } from 'react';
import PreCartItem from '@src/components/shared/header/pre-cart/pre-cart-item/PreCartItem';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { addCart } from '@src/store/features/cartSlice';
import { countCartItems, totalCartValue } from '@src/types/cart';
import { Price } from '@src/components/shared/price/Price';
import CartApiService from '@src/api/CartApiService';
import { handleError } from '@src/error-handler/error-handler';
import { useNavigate } from 'react-router-dom';

const PreCart: React.FC = () => {
  const user = useAppSelector((state) => state.user);
  const cart = useAppSelector((state) => state.cart);
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  useEffect(() => {
    fetchCart(user.id);
  }, []);

  const fetchCart = async (userId: string) => {
    if (!userId) {
      return;
    }
    try {
      const response = await CartApiService.getUserCart(userId);
      dispatch(addCart(response.data));
    } catch (ex) {
      handleError(ex);
    }
  };
  return (
    <div className='shopping-item'>
      <div className='dropdown-cart-header'>
        <span role='items-counter'>{countCartItems(cart)} Items</span>
        {cart?.items && (
          <a role='cart-link' onClick={() => navigate('/cart')}>
            View Cart
          </a>
        )}
      </div>
      <ul className='shopping-list'>
        {cart?.items?.map((item) => (
          <PreCartItem key={item.id} item={item} />
        ))}
      </ul>
      <div className='bottom'>
        <div className='total'>
          <span>Total</span>
          <span role='total-amount' className='total-amount'>
            <Price value={totalCartValue(cart)} />
          </span>
        </div>
        {countCartItems(cart) >= 1 && (
          <a role='checkout-link' className='btn animate' onClick={() => navigate('/cart')}>
            Checkout
          </a>
        )}
      </div>
    </div>
  );
};

export default PreCart;
