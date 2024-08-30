import React from 'react';
import { useNavigate } from 'react-router-dom';

const OrderThankYouPage: React.FC = () => {
  const navigate = useNavigate();
  return (
    <section role='thank-you-page' className='shop login section'>
      <div className='container'>
        <div className='row'>
          <div className='col-lg-6 offset-lg-3 col-12'>
            <div className='login-form'>
              <h2>Thank you!</h2>
              <p>Your order is processing</p>
              <p>
                Visit <a onClick={() => navigate('/orders')}>orders</a> page
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default OrderThankYouPage;
