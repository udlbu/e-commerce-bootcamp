import React from 'react';
import { CartItem } from '@src/types/cart';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import CartApiService from '@src/api/CartApiService';
import { addCart } from '@src/store/features/cartSlice';
import { Price } from '@src/components/shared/price/Price';
import { handleError } from '@src/error-handler/error-handler';

interface PreCartItem {
  item: CartItem;
}

const PreCartItem: React.FC<PreCartItem> = ({ item }) => {
  const user = useAppSelector((state) => state.user);
  const dispatch = useAppDispatch();
  const handleRemoveItemFromCart = async () => {
    try {
      await CartApiService.removeItemFromCart({
        offerId: item.offerId,
        userId: user.id,
      });
      await fetchCart(user.id);
    } catch (ex) {
      handleError(ex);
    }
  };

  const fetchCart = async (userId: string) => {
    const response = await CartApiService.getUserCart(userId);
    dispatch(addCart(response.data));
  };
  return (
    <li>
      <a
        role='remove-btn'
        className='remove'
        title='Remove this item'
        onClick={() => handleRemoveItemFromCart()}
      >
        <i className='fa fa-remove'></i>
      </a>
      <a className='cart-img'>
        <img role='cart-img' src={item.imageUrl} alt='#' />
      </a>
      <h4>
        <a role='product-name'>{item.productName}</a>
      </h4>
      <p role='quantity' className='quantity'>
        {item.quantity}&nbsp;x -{' '}
        <span className='amount'>
          <Price value={item.price} />
        </span>
      </p>
    </li>
  );
};

export default PreCartItem;
