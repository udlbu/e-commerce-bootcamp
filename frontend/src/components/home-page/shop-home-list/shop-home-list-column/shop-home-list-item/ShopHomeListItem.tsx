import React from 'react';
import { Offer } from '@src/types/offer';
import { useNavigate } from 'react-router-dom';
import { Price } from '@src/components/shared/price/Price';
interface ShopHomeListItemProps {
  offer: Offer;
}

const ShopHomeListItem: React.FC<ShopHomeListItemProps> = ({ offer }) => {
  const navigate = useNavigate();
  return (
    <div className='single-list'>
      <div className='row'>
        <div className='col-lg-6 col-md-6 col-12'>
          <div className='list-image overlay'>
            <img src={offer.product.imageUrl} alt='#' />
            <a className='buy'>
              <i className='fa fa-shopping-bag'></i>
            </a>
          </div>
        </div>
        <div className='col-lg-6 col-md-6 col-12 no-padding'>
          <div className='content'>
            <h5 className='title'>
              <a onClick={() => navigate(`/offer/${offer.id}`)}>{offer.product.name}</a>
            </h5>
            <p className='price with-discount'>
              <Price value={offer.price} />
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ShopHomeListItem;
