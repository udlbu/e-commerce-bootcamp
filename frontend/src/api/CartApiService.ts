import axios from 'axios';
import {
  AddItemToCartRequest,
  ChangeItemQuantityRequest,
  RemoveItemFromCartRequest,
} from '@src/types/cart';

axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.put['Content-Type'] = 'application/json';
axios.defaults.headers.get['Accept'] = 'application/json';

class CartApiService {
  addItemToCart = (request: AddItemToCartRequest) => {
    return axios.post(`/api/carts`, request);
  };

  removeItemFromCart = (request: RemoveItemFromCartRequest) => {
    return axios.post(`/api/carts/remove-item`, request);
  };

  getUserCart = (userId: string) => {
    return axios.get(`/api/carts/${userId}`);
  };

  changeQuantity = (request: ChangeItemQuantityRequest) => {
    return axios.put(`/api/carts/change-quantity`, request);
  };
}

export default new CartApiService();
