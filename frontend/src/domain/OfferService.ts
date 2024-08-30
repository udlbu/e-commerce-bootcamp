import { CartState } from '@src/types/cart';
import { Offer } from '@src/types/offer';

export const isOfferAvailable = (cart: CartState, offer: Offer, addedQuantity: number) => {
  const offerInCart = cart?.items?.find((it) => it.offerId === offer.id);
  if (offerInCart && Number(offerInCart.quantity + addedQuantity) > Number(offer.quantity)) {
    return false;
  }
  return true;
};
