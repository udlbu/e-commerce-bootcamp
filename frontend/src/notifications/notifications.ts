import { toast } from 'react-toastify';

const ACCOUNT_MODIFIED = 'Account successfully modified';

const PRODUCT_ADDED = 'Product successfully added';
const PRODUCT_MODIFIED = 'Product successfully modified';
const PRODUCT_DELETED = 'Product successfully deleted';

const OFFER_ADDED = 'Offer successfully added';
const OFFER_MODIFIED = 'Offer successfully modified';
const OFFER_DELETED = 'Offer successfully deleted';
const OFFER_ACTIVATED = 'Offer successfully activated';
const OFFER_DEACTIVATED = 'Offer successfully deactivated';

const CART_ITEM_ADDED = 'Item successfully added';
const CART_ITEM_DELETED = 'Item successfully deleted';

const PASSWORD_CHANGED = 'Password changed';

const NOT_ENOUGH_ITEMS_ON_STOCK = 'Not enough items on stock';
export const notifyAccountModified = () => {
  notifySuccess(ACCOUNT_MODIFIED);
};
export const notifyProductAdded = () => {
  notifySuccess(PRODUCT_ADDED);
};
export const notifyProductModified = () => {
  notifySuccess(PRODUCT_MODIFIED);
};
export const notifyProductDeleted = () => {
  notifySuccess(PRODUCT_DELETED);
};
export const notifyOfferAdded = () => {
  notifySuccess(OFFER_ADDED);
};
export const notifyOfferModified = () => {
  notifySuccess(OFFER_MODIFIED);
};
export const notifyOfferDeleted = () => {
  notifySuccess(OFFER_DELETED);
};
export const notifyOfferActivated = () => {
  notifySuccess(OFFER_ACTIVATED);
};
export const notifyOfferDeactivated = () => {
  notifySuccess(OFFER_DEACTIVATED);
};
export const notifyPasswordChanged = () => {
  notifySuccess(PASSWORD_CHANGED);
};
export const notifyItemAdded = () => {
  notifySuccess(CART_ITEM_ADDED);
};
export const notifyItemDeleted = () => {
  notifySuccess(CART_ITEM_DELETED);
};
export const notifyNotEnoughItemsOnStock = () => {
  notifyWarning(NOT_ENOUGH_ITEMS_ON_STOCK);
};
const notifySuccess = (msg: string) => {
  toast.success(msg, {
    position: toast.POSITION.TOP_RIGHT,
  });
};
const notifyWarning = (msg: string) => {
  toast.warning(msg, {
    position: toast.POSITION.TOP_RIGHT,
  });
};
