import React from 'react';
import { Offer } from '@src/types/offer';
import { useNavigate } from 'react-router-dom';
import OfferModal from '@src/components/shared/modal/OfferModal';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import CartApiService from '@src/api/CartApiService';
import { addCart } from '@src/store/features/cartSlice';
import { Price } from '@src/components/shared/price/Price';
import { handleError } from '@src/error-handler/error-handler';
import { notifyItemAdded, notifyNotEnoughItemsOnStock } from '@src/notifications/notifications';
import { isOfferAvailable } from '@src/domain/OfferService';

interface ProductItemProps {
  offer: Offer;
  isHot: boolean;
  showModal: boolean;
}

const ProductItem: React.FC<ProductItemProps> = ({ offer, isHot, showModal }) => {
  const user = useAppSelector((state) => state.user);
  const cart = useAppSelector((state) => state.cart);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const handleAddItemToCart = async () => {
    try {
      if (!isOfferAvailable(cart, offer, 1)) {
        notifyNotEnoughItemsOnStock();
        return;
      }
      await CartApiService.addItemToCart({
        offerId: offer.id,
        userId: user.id,
        quantity: 1,
      });
      notifyItemAdded();
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
    <div role='single-product' className='single-product'>
      <div className='product-img'>
        <a>
          <img role='productImage' src={offer.product.imageUrl} alt='' />
          {isHot && (
            <span role='hotLabel' className='out-of-stock'>
              Hot
            </span>
          )}
        </a>
        <div className='button-head'>
          <div className='product-action'>
            <a data-toggle='modal' data-target={`#offerModal-${offer.id}`} title='Quick View'>
              <i className='ti-eye'></i>
              <span>Quick Shop</span>
            </a>
            <a title='Wishlist'>
              <i className='ti-heart'></i>
              <span>Add to Wishlist</span>
            </a>
            <a title='Compare'>
              <i className='ti-bar-chart-alt'></i>
              <span>Add to Compare</span>
            </a>
          </div>
          {user && Number(user.id) !== Number(offer.userId) && (
            <div className='product-action-2'>
              <a role='add-to-cart-btn' title='Add to cart' onClick={() => handleAddItemToCart()}>
                Add to cart
              </a>
            </div>
          )}
        </div>
      </div>
      <div className='product-content'>
        <h3 role='productName'>
          <a onClick={() => navigate(`/offer/${offer.id}`)}>{offer.product.name}</a>
        </h3>
        <div className='product-price'>
          <span role='offerPrice'>
            <Price value={offer.price} />
          </span>
        </div>
      </div>
      {showModal && <OfferModal offer={offer} />}
    </div>
  );
};

export default ProductItem;
