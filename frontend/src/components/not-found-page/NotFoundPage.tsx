import React from 'react';
import { useNavigate } from 'react-router-dom';

const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();
  return (
    <>
      <section className='shop shop-form-section section'>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-12 col-12'>
              <div className='shop-form'>
                <h2>Ups, there is nothing here ;)</h2>
              </div>
            </div>
            <div className='col-lg-12 col-12'>
              <div className='shop-form'>
                <button
                  role='go-back-btn'
                  className='btn'
                  type='button'
                  onClick={() => navigate('/')}
                >
                  main page
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  );
};

export default NotFoundPage;
