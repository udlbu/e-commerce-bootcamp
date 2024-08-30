import React, { useEffect, useState } from 'react';
import { CartItem } from '@src/types/cart';
import OfferApiService from '@src/api/OfferApiService';
import { validateBlank } from '@src/components/shared/validators/blank-validator';
import CartApiService from '@src/api/CartApiService';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { addCart } from '@src/store/features/cartSlice';
import { Price } from '@src/components/shared/price/Price';
import { Tooltip } from 'react-tooltip';
import { handleError } from '@src/error-handler/error-handler';
import { notifyItemDeleted } from '@src/notifications/notifications';
import { useNavigate } from 'react-router-dom';

interface CartItemProps {
  item: CartItem;
}

const CartItemRow: React.FC<CartItemProps> = ({ item }) => {
  const user = useAppSelector((state) => state.user);
  const navigate = useNavigate();
  const [offer, setOffer] = useState(null);
  const dispatch = useAppDispatch();
  useEffect(() => {
    const fetchOffer = async () => {
      try {
        const offer = await OfferApiService.getOffer(item.offerId);
        setOffer(offer.data);
      } catch (ex) {
        handleError(ex);
      }
    };
    fetchOffer();
  }, []);
  const incrementQuantity = () => {
    if (item.quantity >= Number(offer?.quantity)) {
      return;
    }
    changeQuantity(item.quantity + 1);
  };

  const decrementQuantity = () => {
    if (item.quantity <= 1) {
      return;
    }
    changeQuantity(item.quantity - 1);
  };

  const handleQuantity = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (
      !validateBlank(event.target.value) ||
      isNaN(Number(event.target.value)) ||
      Number(event.target.value) < 1 ||
      Number(event.target.value) > Number(offer?.quantity)
    ) {
      return;
    }
    changeQuantity(Number(event.target.value));
  };

  const changeQuantity = async (quantity: number) => {
    try {
      await CartApiService.changeQuantity({
        userId: user.id,
        offerId: item.offerId,
        quantity,
      });
      await fetchCart(user.id);
    } catch (ex) {
      handleError(ex);
    }
  };

  const handleRemoveItemFromCart = async () => {
    try {
      await CartApiService.removeItemFromCart({
        offerId: item.offerId,
        userId: user.id,
      });
      notifyItemDeleted();
      await fetchCart(user.id);
    } catch (ex) {
      handleError(ex);
    }
  };

  const fetchCart = async (userId: string) => {
    const response = await CartApiService.getUserCart(userId);
    dispatch(addCart(response.data));
  };

  const isAvailableOnStock = () => {
    return item.quantity < Number(offer?.quantity);
  };

  return (
    <tr role='cart-item-row'>
      <td className='image' data-title='Image'>
        <img role='product-img' src={item.imageUrl} alt='#' />
      </td>
      <td className='product-des' data-title='Description'>
        <p className='product-name'>
          <a role='product-name' onClick={() => navigate(`/offer/${offer.id}`)}>
            {item.productName}
          </a>
        </p>
      </td>
      <td role='price' className='price' data-title='Price'>
        <span>
          <Price value={item.price} />
        </span>
      </td>
      <td className='qty' data-title='Qty'>
        <div className='input-group'>
          <div className='button minus'>
            <button
              role='minus'
              type='button'
              className='btn btn-primary btn-number'
              onClick={decrementQuantity}
            >
              <i className='ti-minus'></i>
            </button>
          </div>
          <input
            role='quantity'
            type='text'
            className='input-number'
            value={item.quantity}
            onChange={handleQuantity}
          />
          <div id={`plus-btn-${item.id}`} className='button plus'>
            <button
              role='plus'
              type='button'
              className='btn btn-primary btn-number'
              data-toggle='tooltip'
              data-placement='top'
              title='Tooltip on top'
              onClick={incrementQuantity}
            >
              <i className='ti-plus'></i>
            </button>
          </div>
          {!isAvailableOnStock() && (
            <Tooltip anchorSelect={`#plus-btn-${item.id}`} place='top'>
              There is no more items on stock
            </Tooltip>
          )}
        </div>
      </td>
      <td role='total-amount' className='total-amount' data-title='Total'>
        <span>
          <Price value={item.quantity * Number(item.price)} />
        </span>
      </td>
      <td className='action' data-title='Remove'>
        <a>
          <i
            role='remove-item-btn'
            className='ti-trash remove-icon'
            onClick={handleRemoveItemFromCart}
          ></i>
        </a>
      </td>
    </tr>
  );
};

export default CartItemRow;
