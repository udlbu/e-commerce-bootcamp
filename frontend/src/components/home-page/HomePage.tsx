import React from 'react';
import Carousel from '@src/components/home-page/carousel/Carousel';
import TrendingItems from '@src/components/home-page/trending-items/TrendingItems';
import HotOffers from '@src/components/home-page/hot-offers/HotOffers';
import ShopHomeList from '@src/components/home-page/shop-home-list/ShopHomeList';
import ShopBlog from '@src/components/home-page/shop-blog/ShopBlog';

const HomePage: React.FC = () => {
  return (
    <>
      <Carousel />
      <TrendingItems />
      <HotOffers />
      <ShopHomeList />
      <ShopBlog />
    </>
  );
};

export default HomePage;
