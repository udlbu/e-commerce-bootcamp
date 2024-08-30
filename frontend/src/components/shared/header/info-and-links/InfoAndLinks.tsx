import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { logout } from '@src/store/features/userSlice';
import UserApiService from '@src/api/UserApiService';
import { handleError } from '@src/error-handler/error-handler';

const InfoAndLinks: React.FC = () => {
  const navigate = useNavigate();
  const user = useAppSelector((state) => state.user);
  const dispatch = useAppDispatch();

  const onLogout = async () => {
    try {
      await UserApiService.logout();
      dispatch(logout());
      navigate('/');
    } catch (ex) {
      handleError(ex);
    }
  };

  return (
    <div className='topbar'>
      <div className='container'>
        <div className='inner-content'>
          <div className='row'>
            <div className='col-lg-4 col-md-12 col-12'>
              <div className='top-left'>
                <ul className='list-main'>
                  <li>
                    <i className='ti-headphone-alt'></i>+44 459-342-605
                  </li>
                  <li>
                    <i className='ti-email'></i>e-shop@mail.com
                  </li>
                </ul>
              </div>
            </div>
            <div className='col-lg-8 col-md-12 col-12'>
              <div className='right-content'>
                <ul className='list-main'>
                  {user && (
                    <li>
                      <i className='ti-alarm-clock'></i>{' '}
                      <a onClick={() => navigate('/orders')}>My Orders</a>
                    </li>
                  )}
                  {user && (
                    <li>
                      <i className='ti-alarm-clock'></i>{' '}
                      <a onClick={() => navigate('/products')}>Products</a>
                    </li>
                  )}
                  {user && (
                    <li>
                      <i className='ti-alarm-clock'></i>{' '}
                      <a onClick={() => navigate('/offers')}>My Assortment</a>
                    </li>
                  )}
                  {user && (
                    <li>
                      <i className='ti-user'></i>{' '}
                      <a onClick={() => navigate('/user')}>Edit Profile</a>
                    </li>
                  )}
                  {user && (
                    <li>
                      <i className='ti-user'></i>{' '}
                      <a onClick={() => navigate('/change-password')}>Change Password</a>
                    </li>
                  )}
                  {!user && (
                    <li>
                      <i className='ti-power-off'></i>
                      <a onClick={() => navigate('/register')}>Register</a>
                    </li>
                  )}
                  {!user && (
                    <li>
                      <i className='ti-power-off'></i>
                      <a onClick={() => navigate('/login')}>Login</a>
                    </li>
                  )}
                  {user && (
                    <li>
                      <i className='ti-power-off'></i>
                      <a onClick={() => onLogout()}>Logout</a>
                    </li>
                  )}
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default InfoAndLinks;
