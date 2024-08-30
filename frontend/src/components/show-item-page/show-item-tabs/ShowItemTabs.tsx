import React from 'react';
import ProductDescriptionTab from './..//show-item-tabs/product-description-tab/ProductDescriptionTab';
import ReviewTab from './../show-item-tabs/review-tab/ReviewTab';
import { Offer } from '@src/types/offer';

interface ShowItemTabsProps {
  offer: Offer;
}

const ShowItemTabs: React.FC<ShowItemTabsProps> = ({ offer }) => {
  return (
    <div className='product-info'>
      <div className='nav-main'>
        <ul className='nav nav-tabs' id='showItemTab' role='tablist'>
          <li className='nav-item' role='nav-item'>
            <a className='nav-link active' data-toggle='tab' href='#description' role='tab'>
              Description
            </a>
          </li>
          <li className='nav-item' role='nav-item'>
            <a className='nav-link' data-toggle='tab' href='#reviews' role='tab'>
              Reviews
            </a>
          </li>
        </ul>
      </div>
      <div className='tab-content' id='show-item-tab'>
        <div className='tab-pane fade show active' id='description' role='tabpanel-description'>
          <div className='tab-single'>
            <ProductDescriptionTab product={offer.product} />
          </div>
        </div>
        <div className='tab-pane fade' id='reviews' role='tabpanel-reviews'>
          <div className='tab-single review-panel'>
            <ReviewTab />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ShowItemTabs;
