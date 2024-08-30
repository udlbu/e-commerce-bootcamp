import React from 'react';

const BlogEntry: React.FC = () => {
  return (
    <div className='col-lg-4 col-md-6 col-12'>
      <div className='shop-single-blog'>
        <img src='https://via.placeholder.com/370x300' alt='#' />
        <div className='content'>
          <p className='date'>23 October , 2023. Monday</p>
          <a className='title'>Example blog entry</a>
          <a className='more-btn'>Continue Reading</a>
        </div>
      </div>
    </div>
  );
};

export default BlogEntry;
