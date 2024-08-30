import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import ShowItemTabs from '@src/components/show-item-page/show-item-tabs/ShowItemTabs';
import { offer } from '@src/test-tools/data';

describe('ShowItemTabs', () => {
  it('should ShowItemTabs component be created', () => {
    // given
    const productDescriptionTab = (
      <BrowserRouter>
        <ShowItemTabs offer={offer} />
      </BrowserRouter>
    );

    // when
    const { getAllByRole } = renderWithProviders(productDescriptionTab);

    // then
    expect(getAllByRole('nav-item').length).toEqual(2);
    expect(getAllByRole('nav-item')[0].textContent).toEqual('Description');
    expect(getAllByRole('nav-item')[1].textContent).toEqual('Reviews');
  });

  it('should ShowItemTabs show description tab when user enter the component', () => {
    // given
    const productDescriptionTab = (
      <BrowserRouter>
        <ShowItemTabs offer={offer} />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(productDescriptionTab);

    // then
    expect(getByRole('tabpanel-description')).toHaveClass('active');
    expect(getByRole('tabpanel-reviews')).not.toHaveClass('active');
  });
});
