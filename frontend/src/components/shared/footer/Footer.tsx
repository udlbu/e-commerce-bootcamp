import React from 'react';
import payment from '@assets/images/payments.png';
import logo from '@assets/images/logo.png';

const Footer: React.FC = () => {
  return (
    <footer className='footer'>
      <div className='footer-top section'>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-5 col-md-6 col-12'>
              <div className='single-footer about'>
                <div className='logo'>
                  <a>
                    <img src={logo} alt='' />
                  </a>
                </div>
                <p className='text'>Bootcamp description</p>
                <p className='call'>
                  Got Question? Call us 24/7
                  <span>
                    <a>+44 459-342-605</a>
                  </span>
                </p>
              </div>
            </div>
            <div className='col-lg-2 col-md-6 col-12'>
              <div className='single-footer links'>
                <h4>Information</h4>
                <ul>
                  <li>
                    <a>About Us</a>
                  </li>
                  <li>
                    <a>Faq</a>
                  </li>
                  <li>
                    <a>Terms & Conditions</a>
                  </li>
                  <li>
                    <a>Contact Us</a>
                  </li>
                  <li>
                    <a>Help</a>
                  </li>
                </ul>
              </div>
            </div>
            <div className='col-lg-2 col-md-6 col-12'>
              <div className='single-footer links'>
                <h4>Customer Service</h4>
                <ul>
                  <li>
                    <a>Payment Methods</a>
                  </li>
                  <li>
                    <a>Money-back</a>
                  </li>
                  <li>
                    <a>Returns</a>
                  </li>
                  <li>
                    <a>Shipping</a>
                  </li>
                  <li>
                    <a>Privacy Policy</a>
                  </li>
                </ul>
              </div>
            </div>
            <div className='col-lg-3 col-md-6 col-12'>
              <div className='single-footer social'>
                <h4>Get In Touch</h4>
                <div className='contact'>
                  <ul>
                    <li>London 1a Downing Street</li>
                    <li>England</li>
                    <li>e-shop@mail.com</li>
                    <li>+44 459-342-605</li>
                  </ul>
                </div>
                <ul>
                  <li>
                    <a>
                      <i className='ti-facebook'></i>
                    </a>
                  </li>
                  <li>
                    <a>
                      <i className='ti-twitter'></i>
                    </a>
                  </li>
                  <li>
                    <a>
                      <i className='ti-flickr'></i>
                    </a>
                  </li>
                  <li>
                    <a>
                      <i className='ti-instagram'></i>
                    </a>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div className='copyright'>
        <div className='container'>
          <div className='inner'>
            <div className='row'>
              <div className='col-lg-6 col-12'>
                <div className='left'>
                  <p>
                    Copyright © 2023 <a>Lukasz Wyspianski</a> - All Rights Reserved.
                  </p>
                </div>
              </div>
              <div className='col-lg-6 col-12'>
                <div className='right'>
                  <img src={payment} alt='' />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
