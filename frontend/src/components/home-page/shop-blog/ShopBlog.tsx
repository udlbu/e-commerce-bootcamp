import React from 'react';
import BlogEntry from '@src/components/home-page/shop-blog/blog-entry/BlogEntry';

const ShopBlog: React.FC = () => {
  return (
    <section className='shop-blog section'>
      <div className='container'>
        <div className='row'>
          <div className='col-12'>
            <div className='section-title'>
              <h2>From Our Blog</h2>
            </div>
          </div>
        </div>
        <div className='row'>
          <BlogEntry />
          <BlogEntry />
          <BlogEntry />
        </div>
      </div>
    </section>
  );
};

export default ShopBlog;
