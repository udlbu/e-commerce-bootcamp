import React from 'react';
import ShopHomeListColumn from '@src/components/home-page/shop-home-list/shop-home-list-column/ShopHomeListColumn';

const ShopHomeList: React.FC = () => {
  return (
    <section className='shop-home-list section'>
      <div className='container'>
        <div className='row'>
          <ShopHomeListColumn label='On Sale' category='TOYS' />
          <ShopHomeListColumn label='Best Seller' category='BOOKS' />
          <ShopHomeListColumn label='Top Viewed' category='SPORTS' />
        </div>
      </div>
    </section>
  );
};

export default ShopHomeList;
