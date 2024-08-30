import React, { useEffect, useState } from 'react';
import TrendingInCategoryOffers from '@src/components/home-page/trending-items/trending-in-category-offers/TrendingInCategoryOffers';
import { ProductCategory } from '@src/types/product';
import clsx from 'clsx';
import OfferApiService from '@src/api/OfferApiService';
import { handleError } from '@src/error-handler/error-handler';

const TrendingItems: React.FC = () => {
  const firstCategory = Object.keys(ProductCategory)[0];
  const [activeCategory, setActiveCategory] = useState(firstCategory);
  const [offers, setOffers] = useState([]);
  const changeTab = (category: string) => {
    setActiveCategory(category);
    getOffers(category);
  };
  useEffect(() => {
    getOffers(Object.keys(ProductCategory)[0]);
  }, []);

  const getOffers = async (category: string) => {
    const request = { page: 0, pageSize: 4, productCategory: category };

    try {
      const response = await OfferApiService.getActiveOffers(request);
      setOffers(response.data.offers);
    } catch (err) {
      handleError(err);
    }
  };
  return (
    <div className='product-area section'>
      <div className='container'>
        <div className='row'>
          <div className='col-12'>
            <div role='trendingTitle' className='section-title'>
              <h2>Trending Item</h2>
            </div>
          </div>
        </div>
        <div className='row'>
          <div className='col-12'>
            <div className='product-info'>
              <div className='nav-main'>
                <ul className='nav nav-tabs' id='categories' role='categoriesTabs'>
                  {Object.keys(ProductCategory).map((it) => {
                    return (
                      <li key={it} role='categoryTab' className='nav-item'>
                        <a
                          className={clsx('nav-link', it === activeCategory ? 'active' : '')}
                          data-toggle='tab'
                          role='tabLink'
                          onClick={() => changeTab(it)}
                        >
                          {ProductCategory[it as keyof typeof ProductCategory]}
                        </a>
                      </li>
                    );
                  })}
                </ul>
              </div>
              <div className='tab-content' id='categoryTabContent'>
                <TrendingInCategoryOffers offers={offers} />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default TrendingItems;
