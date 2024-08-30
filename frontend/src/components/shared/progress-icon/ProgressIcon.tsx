import React from 'react';

const ProgressIcon: React.FC = () => {
  return (
    <div role='processing-icon' className='progress-btn active'>
      <svg
        className='progress circle-loader'
        width='40'
        height='40'
        version='1.1'
        xmlns='http://www.w3.org/2000/svg'
      >
        <circle cx='20' cy='20' r='13' />
      </svg>
    </div>
  );
};

export default ProgressIcon;
