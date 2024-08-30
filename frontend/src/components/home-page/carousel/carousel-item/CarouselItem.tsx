import React from 'react';

const CarouselItem: React.FC = () => {
  return (
    <div className='big-content'>
      <div className='inner'>
        <h4 className='title'>ElectroBazaar: Your Gateway to Cutting-Edge Electronics</h4>
        <p className='des'>
          Welcome to our Electronics Wonderland!
          <br />
          Discover a vast array of innovative gadgets and devices that will electrify your world.
          From state-of-the-art smartphones to immersive audio systems, smart home solutions, and
          precision-engineered accessories, our Electronics category is your ultimate destination
          for all things tech. Whether you&apos;re a seasoned tech enthusiast or a casual user, our
          carefully curated selection guarantees top-notch quality and the latest advancements in
          electronics. Explore, compare, and choose from a variety of renowned brands that are
          synonymous with excellence in the industry. Upgrade your lifestyle with the power of
          electronics today!
          <br /> Now let come here and grab it now !
        </p>
        <div className='button'>
          <a href='/listing?category=ELECTRONICS' className='btn'>
            Shop Now
          </a>
        </div>
      </div>
    </div>
  );
};

export default CarouselItem;
