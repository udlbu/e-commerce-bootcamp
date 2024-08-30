import React, { useRef, useState } from 'react';
import OwlCarousel from 'react-owl-carousel';
import { Offer } from '@src/types/offer';
import styles from './OfferModal.module.scss';
import { validateBlank } from '@src/components/shared/validators/blank-validator';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import CartApiService from '@src/api/CartApiService';
import { addCart } from '@src/store/features/cartSlice';
import { Tooltip } from 'react-tooltip';
import { Price } from '@src/components/shared/price/Price';
import { handleError } from '@src/error-handler/error-handler';
import { notifyNotEnoughItemsOnStock } from '@src/notifications/notifications';
import { isOfferAvailable } from '@src/domain/OfferService';

interface OfferModalProps {
  offer: Offer;
}

const OfferModal: React.FC<OfferModalProps> = ({ offer }) => {
  const cart = useAppSelector((state) => state.cart);
  const user = useAppSelector((state) => state.user);
  const dispatch = useAppDispatch();
  const [quantity, setQuantity] = useState(1);
  const closeBtn = useRef(null);
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
      await CartApiService.addItemToCart({
        offerId: offer.id,
        userId: user.id,
        quantity: quantity,
      });
      await fetchCart(user.id);
    } catch (ex) {
      handleError(ex);
    } finally {
      closeBtn.current?.click();
    }
  };

  const fetchCart = async (userId: string) => {
    const response = await CartApiService.getUserCart(userId);
    dispatch(addCart(response.data));
  };

  const isAvailableOnStock = () => {
    return quantity < Number(offer?.quantity);
  };

  return (
    <div className='modal fade' id={`offerModal-${offer.id}`} role='dialog'>
      <div className='modal-dialog' role='document'>
        <div className='modal-content'>
          <div className='modal-header'>
            <button
              ref={closeBtn}
              role='closeBtn'
              type='button'
              className='close'
              data-dismiss='modal'
              aria-label='Close'
            >
              <span className='ti-close' aria-hidden='true'></span>
            </button>
          </div>
          <div className='modal-body'>
            <div className='row no-gutters'>
              <div className='col-lg-6 col-md-12 col-sm-12 col-xs-12'>
                <div className='product-gallery'>
                  <OwlCarousel
                    className='quickview-slider-active owl-theme'
                    loop
                    margin={1}
                    nav
                    items={1}
                  >
                    {[1, 2, 3].map((it) => {
                      return (
                        <div key={it} className='single-slider'>
                          <img
                            role='offerImage'
                            className={styles.imgSize}
                            src={offer.product.imageUrl}
                            alt='#'
                          />
                        </div>
                      );
                    })}
                  </OwlCarousel>
                </div>
              </div>
              <div className='col-lg-6 col-md-12 col-sm-12 col-xs-12'>
                <div className='quickview-content'>
                  <h2>{offer.product.name}</h2>
                  <div className='quickview-ratting-review'>
                    <div className='quickview-ratting-wrap'>
                      <div className='quickview-ratting'>
                        <i className='yellow fa fa-star'></i>
                        <i className='yellow fa fa-star'></i>
                        <i className='yellow fa fa-star'></i>
                        <i className='yellow fa fa-star'></i>
                        <i className='fa fa-star'></i>
                      </div>
                      <a> (1 customer review)</a>
                    </div>
                    <div className='quickview-stock'>
                      <span role='stockQuantity'>
                        <i className='fa fa-check-circle-o'></i>
                        {offer.quantity} items in stock
                      </span>
                    </div>
                  </div>
                  <h3>
                    <Price value={offer.price} />
                  </h3>
                  <div className='quickview-peragraph'>
                    <p role='offerProductDesc'>{offer.product.description}</p>
                  </div>
                  {user && Number(user.id) !== Number(offer.userId) && (
                    <div className='quantity'>
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
                        <div className='button plus'>
                          <button
                            role='plus'
                            type='button'
                            className='btn btn-primary btn-number plus-quantity-btn'
                            onClick={incrementQuantity}
                          >
                            <i className='ti-plus'></i>
                          </button>
                        </div>
                        {!isAvailableOnStock() && (
                          <Tooltip anchorSelect='.plus-quantity-btn' place='top'>
                            There is no more items on stock
                          </Tooltip>
                        )}
                      </div>
                    </div>
                  )}
                  {user && Number(user.id) !== Number(offer.userId) && (
                    <div role='add-to-cart' className='add-to-cart'>
                      <a
                        className='btn'
                        role='add-to-cart-modal-btn'
                        title='Add to cart'
                        onClick={() => handleAddItemToCart()}
                      >
                        Add to cart
                      </a>
                      <a className='btn min'>
                        <i className='ti-heart'></i>
                      </a>
                      <a className='btn min'>
                        <i className='fa fa-compress'></i>
                      </a>
                    </div>
                  )}
                  <div className='default-social'>
                    <h4 className='share-now'>Share:</h4>
                    <ul>
                      <li>
                        <a className='facebook'>
                          <i className='fa fa-facebook'></i>
                        </a>
                      </li>
                      <li>
                        <a className='twitter'>
                          <i className='fa fa-twitter'></i>
                        </a>
                      </li>
                      <li>
                        <a className='youtube'>
                          <i className='fa fa-pinterest-p'></i>
                        </a>
                      </li>
                      <li>
                        <a className='dribbble'>
                          <i className='fa fa-google-plus'></i>
                        </a>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OfferModal;
