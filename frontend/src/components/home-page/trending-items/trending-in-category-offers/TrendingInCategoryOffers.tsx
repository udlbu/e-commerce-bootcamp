import React from 'react';
import ProductItem from '@src/components/shared/product-item/ProductItem';
import clsx from 'clsx';
import styles from './TrendingInCategoryOffers.module.scss';
import { Offer } from '@src/types/offer';

interface TrendingInCategoryOffersProps {
  offers: Offer[];
}

const TrendingInCategoryOffers: React.FC<TrendingInCategoryOffersProps> = ({ offers }) => {
  return (
    <div className='tab-pane fade show active' role='tabpanel'>
      <div className='tab-single'>
        <div className='row'>
          {offers.map((it) => {
            return (
              <div
                key={it.id}
                role='trendingOffer'
                className={clsx(styles.marginTop, 'col-xl-3 col-lg-4 col-md-4 col-12')}
              >
                <ProductItem offer={it} isHot={false} showModal={true} />
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
export default TrendingInCategoryOffers;
