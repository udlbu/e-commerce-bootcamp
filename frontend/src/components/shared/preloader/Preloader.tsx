import React from 'react';

const Preloader: React.FC = () => {
  return (
    <div role='preloader' className='preloader'>
      <div className='preloader-inner'>
        <div className='preloader-icon'>
          <span></span>
          <span></span>
        </div>
      </div>
    </div>
  );
};

export default Preloader;
