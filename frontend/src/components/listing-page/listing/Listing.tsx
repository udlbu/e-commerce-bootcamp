import React from 'react';
import ProductItem from '@src/components/shared/product-item/ProductItem';
import { useAppSelector } from '@src/store/hooks';
import Preloader from '@src/components/shared/preloader/Preloader';

interface ListingProps {
  processing: boolean;
}

const Listing: React.FC<ListingProps> = ({ processing }) => {
  const listingPage = useAppSelector((state) => state.listingOffers);
  return (
    <>
      {processing && (
        <div className='preloader-min-height'>
          <Preloader />
        </div>
      )}
      {!processing && listingPage.data?.length > 0 && (
        <div className='row'>
          {listingPage.data.map((it) => {
            return (
              <div key={it.id} role='offerItem' className='col-lg-4 col-md-6 col-12'>
                <ProductItem offer={it} isHot={false} showModal={true} />
              </div>
            );
          })}
        </div>
      )}
      {(!listingPage.data || listingPage.data.length == 0) && (
        <div role='empty-offer-message' className='row'>
          There are no offers in category
        </div>
      )}
    </>
  );
};

export default Listing;
