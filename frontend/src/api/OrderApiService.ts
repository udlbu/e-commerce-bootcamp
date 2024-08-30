import axios from 'axios';
import { SearchOrdersRequest, SubmitOrderRequest } from '@src/types/order';

axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.put['Content-Type'] = 'application/json';
axios.defaults.headers.get['Accept'] = 'application/json';

class OrderApiService {
  submitOrder = (request: SubmitOrderRequest) => {
    return axios.post(`/api/orders`, request);
  };

  searchOffers = (request: SearchOrdersRequest) => {
    return axios.post(`/api/orders/search`, request);
  };
}

export default new OrderApiService();
