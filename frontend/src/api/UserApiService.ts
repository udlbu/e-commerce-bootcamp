import axios from 'axios';
import { AddUserRequest, ChangePasswordRequest, ModifyUserRequest } from '@src/types/user';

axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.put['Content-Type'] = 'application/json';
axios.defaults.headers.get['Accept'] = 'application/json';

class UserApiService {
  addUser = (addUserRequest: AddUserRequest) => {
    return axios.post(`/api/users`, addUserRequest);
  };

  modifyUser = (modifyUserRequest: ModifyUserRequest) => {
    return axios.put(`/api/users`, modifyUserRequest);
  };

  getUserCurrent = () => {
    return axios.get(`/api/users/current`);
  };

  changePassword = (id: string, changePasswordRequest: ChangePasswordRequest) => {
    return axios.put(`/api/users/${id}/change-password`, changePasswordRequest);
  };

  authenticate = (username: string, password: string) => {
    return axios.post(`/api/authenticate`, { username, password });
  };

  logout = () => {
    return axios.post(`/api/logout`, {});
  };
}

export default new UserApiService();
