import React, { useState } from 'react';
import { Offer } from '@src/types/offer';
import { validateBlank } from '@src/components/shared/validators/blank-validator';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import CartApiService from '@src/api/CartApiService';
import { addCart } from '@src/store/features/cartSlice';
import { Price } from '@src/components/shared/price/Price';
import { Tooltip } from 'react-tooltip';
import { handleError } from '@src/error-handler/error-handler';
import Button from '@src/components/shared/button/Button';
import { notifyItemAdded, notifyNotEnoughItemsOnStock } from '@src/notifications/notifications';
import { isOfferAvailable } from '@src/domain/OfferService';

interface OfferInfoProps {
  offer: Offer;
}

const OfferInfo: React.FC<OfferInfoProps> = ({ offer }) => {
  const user = useAppSelector((state) => state.user);
  const cart = useAppSelector((state) => state.cart);
  const dispatch = useAppDispatch();
  const [quantity, setQuantity] = useState(1);
  const [processing, setProcessing] = useState(false);
  const incrementQuantity = () => {
    if (quantity >= Number(offer.quantity)) {
      return;
    }
    setQuantity(quantity + 1);
  };

  const decrementQuantity = () => {
    if (quantity <= 1) {
      return;
    }
    setQuantity(quantity - 1);
  };

  const handleQuantity = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (
      !validateBlank(event.target.value) ||
      isNaN(Number(event.target.value)) ||
      Number(event.target.value) < 1 ||
      Number(event.target.value) > Number(offer.quantity)
    ) {
      return;
    }
    setQuantity(Number(event.target.value));
  };

  const handleAddItemToCart = async () => {
    try {
      if (!isOfferAvailable(cart, offer, quantity)) {
        notifyNotEnoughItemsOnStock();
        return;
      }
      setProcessing(true);
      await CartApiService.addItemToCart({
        offerId: offer.id,
        userId: user.id,
        quantity: quantity,
      });
      notifyItemAdded();
      await fetchCart(user.id);
    } catch (ex) {
      handleError(ex);
    } finally {
      setProcessing(false);
    }
  };

  const isAvailableOnStock = () => {
    return quantity < Number(offer?.quantity);
  };

  const fetchCart = async (userId: string) => {
    const response = await CartApiService.getUserCart(userId);
    dispatch(addCart(response.data));
  };

  return (
    <div className='product-des'>
      <div className='short'>
        <h4 role='productName'>{offer.product.name}</h4>
        <div className='rating-main'>
          <ul className='rating'>
            <li>
              <i className='fa fa-star'></i>
            </li>
            <li>
              <i className='fa fa-star'></i>
            </li>
            <li>
              <i className='fa fa-star'></i>
            </li>
            <li>
              <i className='fa fa-star-half-o'></i>
            </li>
            <li className='dark'>
              <i className='fa fa-star-o'></i>
            </li>
          </ul>
          <a className='total-review'>(102) Review</a>
        </div>
        <p className='price'>
          <span role='priceDiscount' className='discount'>
            <Price value={offer.price} />
          </span>
          <s role='price'>
            <Price value={offer.price} />
          </s>
        </p>
        <p role='description' className='description'>
          {offer.product.description}
        </p>
      </div>
      <div className='product-buy'>
        {user && Number(user.id) !== Number(offer.userId) && (
          <div className='quantity'>
            <h6>Quantity :</h6>
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
                value={quantity}
                onChange={handleQuantity}
              />
              <div id={`plus-btn-${offer.id}`} className='button plus'>
                <button
                  role='plus'
                  type='button'
                  className='btn btn-primary btn-number'
                  onClick={incrementQuantity}
                >
                  <i className='ti-plus'></i>
                </button>
              </div>
              {!isAvailableOnStock() && (
                <Tooltip anchorSelect={`#plus-btn-${offer.id}`} place='top'>
                  There is no more items on stock
                </Tooltip>
              )}
            </div>
          </div>
        )}
        {user && Number(user.id) !== Number(offer.userId) && (
          <div role='add-to-cart' className='add-to-cart'>
            <Button
              role='add-to-cart-offer-details-btn'
              text='Add to cart'
              onClick={() => handleAddItemToCart()}
              processing={processing}
            />
            <a className='btn min'>
              <i className='ti-heart'></i>
            </a>
            <a className='btn min'>
              <i className='fa fa-compress'></i>
            </a>
          </div>
        )}
        <p className='cat'>
          Category:&nbsp;
          <span role='productCategory'>
            <strong>{offer.product.category.toLowerCase()}</strong>
          </span>
        </p>
        <p role='availability' className='availability'>
          Availability:&nbsp;<strong>{offer.quantity}</strong> Products In Stock
        </p>
      </div>
    </div>
  );
};

export default OfferInfo;
