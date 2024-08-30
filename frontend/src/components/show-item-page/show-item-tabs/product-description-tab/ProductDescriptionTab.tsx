import React from 'react';
import { Product } from '@src/types/product';

interface ProductDescriptionTabProps {
  product: Product;
}

const ProductDescriptionTab: React.FC<ProductDescriptionTabProps> = ({ product }) => {
  return (
    <div className='row'>
      <div className='col-12'>
        <div className='single-des'>
          <p>{product.description}</p>
        </div>
      </div>
    </div>
  );
};

export default ProductDescriptionTab;
