import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';

describe('Breadcrumbs', () => {
  it('should Breadcrumbs component be created', () => {
    // given
    const elements = [
      { label: 'Label 1', link: 'Link1' },
      { label: 'Label 2', link: 'Link1' },
      { label: 'Label 3', link: 'Link1' },
    ];
    const breadcrumbs = (
      <BrowserRouter>
        <Breadcrumbs elements={elements} />
      </BrowserRouter>
    );

    // when
    const { getAllByRole, getByRole } = renderWithProviders(breadcrumbs);

    // then
    expect(getAllByRole('breadcrumb').length).toEqual(elements.length - 1);
    expect(getByRole('lastBreadcrumb')).toBeInTheDocument();
    expect(getByRole('lastBreadcrumb')).toHaveAttribute('class', 'active');
  });
});
