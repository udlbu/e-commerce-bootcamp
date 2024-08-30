import React from 'react';
import CarouselItem from '@src/components/home-page/carousel/carousel-item/CarouselItem';
import OwlCarousel from 'react-owl-carousel';

const Carousel: React.FC = () => {
  return (
    <section className='hero-area4'>
      <div className='container'>
        <div className='row'>
          <div className='col-12'>
            <OwlCarousel
              className='home-slider-4 owl-theme'
              loop
              margin={1}
              nav
              items={1}
              navText={['<i class="ti-angle-left"></i>', '<i class="ti-angle-right"></i>']}
            >
              <CarouselItem />
              <CarouselItem />
              <CarouselItem />
            </OwlCarousel>
            ;
          </div>
        </div>
      </div>
    </section>
  );
};

export default Carousel;
