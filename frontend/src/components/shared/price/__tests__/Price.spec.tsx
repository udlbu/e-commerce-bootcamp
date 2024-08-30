import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import { Price } from '@src/components/shared/price/Price';

describe('Price', () => {
  [
    {
      value: 2,
      expected: '$2.00',
    },
    {
      value: '2',
      expected: '$2.00',
    },
    {
      value: '2.0',
      expected: '$2.00',
    },
    {
      value: '2.00',
      expected: '$2.00',
    },
    {
      value: '2.004',
      expected: '$2.00',
    },
    {
      value: '2.006',
      expected: '$2.01',
    },
    {
      value: '',
      expected: '$0.00',
    },
    {
      value: 'a',
      expected: '-',
    },
    {
      value: 'a2',
      expected: '-',
    },
  ].forEach((elem) => {
    it(`should Price component be created [value: ${elem.value}]`, () => {
      // given
      const price = (
        <BrowserRouter>
          <Price value={elem.value} />
        </BrowserRouter>
      );

      // when
      const component = renderWithProviders(price);

      // then
      expect(component.queryByText(elem.expected)).toBeInTheDocument();
    });
  });
});
