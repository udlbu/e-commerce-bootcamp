import React from 'react';
import OwlCarousel from 'react-owl-carousel';
import { Product } from '@src/types/product';

interface ProductGalleryProps {
  product: Product;
}
const ProductGallery: React.FC<ProductGalleryProps> = ({ product }) => {
  return (
    <div className='product-gallery'>
      <OwlCarousel
        className='home-slider-4 owl-theme'
        loop
        margin={1}
        nav
        items={1}
        navText={['<i class="ti-angle-left"></i>', '<i class="ti-angle-right"></i>']}
      >
        <img src={product.imageUrl} />
        <img src={product.imageUrl} />
        <img src={product.imageUrl} />
      </OwlCarousel>
    </div>
  );
};

export default ProductGallery;
