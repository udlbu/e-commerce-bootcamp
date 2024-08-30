import React from 'react';

const CartHeader: React.FC = () => {
  return (
    <tr className='main-hading'>
      <th role='product-header'>PRODUCT</th>
      <th role='name-header'>NAME</th>
      <th role='unit-price-header' className='text-center'>
        UNIT PRICE
      </th>
      <th role='quantity-header' className='text-center'>
        QUANTITY
      </th>
      <th role='total-header' className='text-center'>
        TOTAL
      </th>
      <th role='trash-icon-header' className='text-center'>
        <i className='ti-trash remove-icon'></i>
      </th>
    </tr>
  );
};

export default CartHeader;
