import React from 'react';

interface PriceProps {
  value: string | number;
}

export const Price: React.FC<PriceProps> = ({ value }) => {
  const formatPrice = (value: string | number) => {
    try {
      const num = Number(value);
      if (isNaN(num)) {
        return '-';
      }
      return `$${num.toFixed(2)}`;
    } catch (ex) {
      console.error(`Invalid price: ${value}`);
      return '-';
    }
  };
  return <>{formatPrice(value)}</>;
};
