import axios from 'axios';
import { AddOfferRequest, ModifyOfferRequest, SearchOffersRequest } from '@src/types/offer';

axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.put['Content-Type'] = 'application/json';
axios.defaults.headers.get['Accept'] = 'application/json';

class ProductApiService {
  getOffer = (id: string) => {
    return axios.get(`/api/offers/${id}`);
  };

  addOffer = (addOfferRequest: AddOfferRequest) => {
    return axios.post(`/api/offers`, addOfferRequest);
  };

  modifyOffer = (modifyOfferRequest: ModifyOfferRequest) => {
    return axios.put(`/api/offers`, modifyOfferRequest);
  };

  deleteOffer = (id: string) => {
    return axios.delete(`/api/offers/${id}`);
  };

  getActiveOffers = (searchOffersRequest: SearchOffersRequest) => {
    return axios.post(`/api/offers/search`, searchOffersRequest);
  };

  getCurrentUserOffers = (page: number, pageSize = 10) => {
    return axios.get(`/api/offers/user/search?page=${page}&pageSize=${pageSize}`);
  };

  activateOffer = (id: string) => {
    return axios.put(`/api/offers/${id}/activate`);
  };

  deactivateOffer = (id: string) => {
    return axios.put(`/api/offers/${id}/deactivate`);
  };
}

export default new ProductApiService();
