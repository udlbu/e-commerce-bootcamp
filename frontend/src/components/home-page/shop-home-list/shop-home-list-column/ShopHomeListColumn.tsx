import React, { useEffect, useState } from 'react';
import ShopHomeListItem from '@src/components/home-page/shop-home-list/shop-home-list-column/shop-home-list-item/ShopHomeListItem';
import OfferApiService from '@src/api/OfferApiService';
import { handleError } from '@src/error-handler/error-handler';

interface ShopHomeListColumnProps {
  label: string;
  category: string;
}

const ShopHomeListColumn: React.FC<ShopHomeListColumnProps> = ({ label, category }) => {
  const [offers, setOffers] = useState([]);
  useEffect(() => {
    const request = { page: 0, pageSize: 3, productCategory: category };
    OfferApiService.getActiveOffers(request)
      .then((response) => {
        setOffers(response.data.offers);
      })
      .catch((err) => handleError(err));
  }, []);
  return (
    <div className='col-lg-4 col-md-6 col-12'>
      <div className='row'>
        <div className='col-12'>
          <div className='shop-section-title'>
            <h1>{label}</h1>
          </div>
        </div>
      </div>
      {offers.map((it) => {
        return <ShopHomeListItem key={it.id} offer={it} />;
      })}
    </div>
  );
};

export default ShopHomeListColumn;
