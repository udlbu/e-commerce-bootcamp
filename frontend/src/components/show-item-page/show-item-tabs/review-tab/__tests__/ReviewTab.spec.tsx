import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import ReviewTab from '@src/components/show-item-page/show-item-tabs/review-tab/ReviewTab';

describe('ReviewTab', () => {
  it('should ReviewTab component be created', () => {
    // given
    const reviewTab = (
      <BrowserRouter>
        <ReviewTab />
      </BrowserRouter>
    );

    // when
    const { container } = renderWithProviders(reviewTab);

    // then
    expect(container).toBeTruthy();
  });
});
