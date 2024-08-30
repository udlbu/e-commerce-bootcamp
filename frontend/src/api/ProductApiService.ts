import axios from 'axios';
import { AddProductRequest, ModifyProductRequest } from '@src/types/product';

axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.put['Content-Type'] = 'application/json';
axios.defaults.headers.get['Accept'] = 'application/json';

class ProductApiService {
  addProduct = (addProductRequest: AddProductRequest) => {
    return axios.post(`/api/products`, addProductRequest);
  };

  modifyProduct = (modifyProductRequest: ModifyProductRequest) => {
    return axios.put(`/api/products`, modifyProductRequest);
  };

  getProducts = (page: number, pageSize = 10) => {
    return axios.get(`/api/products?page=${page}&pageSize=${pageSize}`);
  };

  deleteProduct = (id: string) => {
    return axios.delete(`/api/products/${id}`);
  };
}

export default new ProductApiService();
