import React from 'react';
import InfoAndLinks from '@src/components/shared/header/info-and-links/InfoAndLinks';
import SearchBar from '@src/components/shared/header/search-bar/SearchBar';
import { useNavigate } from 'react-router-dom';

const Header: React.FC = () => {
  const navigate = useNavigate();
  return (
    <header className='header shop v3'>
      <InfoAndLinks />
      <SearchBar />
      <div className='header-inner'>
        <div className='container'>
          <div className='cat-nav-head'>
            <div className='row'>
              <div className='col-12'>
                <div className='menu-area'>
                  <nav className='navbar navbar-expand-lg'>
                    <div className='navbar-collapse'>
                      <div className='nav-inner'>
                        <ul className='nav main-menu menu navbar-nav'>
                          <li className='active'>
                            <a onClick={() => navigate('/')}>Home</a>
                          </li>
                          <li>
                            <a onClick={() => navigate('/listing')}>Shop</a>
                          </li>
                          <li>
                            <a>
                              Blog
                              <span className='new'>New</span>
                            </a>
                          </li>
                          <li>
                            <a>Contact Us</a>
                          </li>
                        </ul>
                      </div>
                    </div>
                  </nav>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
