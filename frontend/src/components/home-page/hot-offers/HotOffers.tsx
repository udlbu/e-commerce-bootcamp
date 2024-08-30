import React, { useEffect, useState } from 'react';
import ProductItem from '@src/components/shared/product-item/ProductItem';
import OfferApiService from '@src/api/OfferApiService';
import { handleError } from '@src/error-handler/error-handler';

const HotOffers: React.FC = () => {
  const [offers, setOffers] = useState([]);
  useEffect(() => {
    const request = { page: 0, pageSize: 4 };
    OfferApiService.getActiveOffers(request)
      .then((response) => {
        setOffers(response.data.offers);
      })
      .catch((err) => handleError(err));
  }, []);

  return (
    <div className='product-area most-popular section'>
      <div className='container'>
        <div className='row'>
          <div className='col-12'>
            <div className='section-title'>
              <h2 role='hot-offers-role'>Hot Items</h2>
            </div>
          </div>
        </div>
        <div className='row'>
          {offers.map((it) => {
            return (
              <div className='col-3' key={it.id}>
                <ProductItem offer={it} isHot={true} showModal={true} />
              </div>
            );
          })}
          <div></div>
        </div>
      </div>
    </div>
  );
};

export default HotOffers;
