import React from 'react';
import PreCart from '@src/components/shared/header/pre-cart/PreCart';
import logo from '@assets/images/logo.png';
import { useAppSelector } from '@src/store/hooks';
import { countCartItems } from '@src/types/cart';
import { useNavigate } from 'react-router-dom';

const SearchBar: React.FC = () => {
  const user = useAppSelector((state) => state.user);
  const cart = useAppSelector((state) => state.cart);
  const navigate = useNavigate();

  return (
    <div className='middle-inner'>
      <div className='container'>
        <div className='row'>
          <div className='col-lg-2 col-md-2 col-12'>
            <div className='logo'>
              <a>
                <img src={logo} alt='' />
              </a>
            </div>
            <div className='search-top'>
              <div className='top-search'>
                <a>
                  <i className='ti-search'></i>
                </a>
              </div>
              <div className='search-top'>
                <div className='search-form'>
                  <input type='text' placeholder='Search here...' name='search' />
                  <button value='search' type='submit'>
                    <i className='ti-search'></i>
                  </button>
                </div>
              </div>
            </div>
            <div className='mobile-nav'></div>
          </div>
          <div role='search-bar-component' className='col-lg-8 col-md-7 col-12'>
            <div className='search-bar-top'>
              <div className='search-bar'>
                <input name='search' placeholder='Search...' type='search' />
                <button className='btn'>
                  <i className='ti-search'></i>
                </button>
              </div>
            </div>
          </div>
          {user && (
            <div className='col-lg-2 col-md-3 col-12'>
              <div className='right-bar'>
                <div role='favourite-icon' className='single-bar'>
                  <a className='single-icon'>
                    <i className='fa fa-heart-o' aria-hidden='true'></i>
                  </a>
                </div>
                <div className='single-bar'>
                  <a role='user-icon' className='single-icon'>
                    <i
                      className='fa fa-user-circle-o'
                      aria-hidden='true'
                      onClick={() => navigate('/user')}
                    ></i>
                  </a>
                </div>
                <div role='pre-cart-component' className='single-bar shopping'>
                  <a className='single-icon'>
                    <i className='ti-bag'></i>{' '}
                    <span role='total-items-count' className='total-count'>
                      {countCartItems(cart)}
                    </span>
                  </a>
                  <PreCart />
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SearchBar;
