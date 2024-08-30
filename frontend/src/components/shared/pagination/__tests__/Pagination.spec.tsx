import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import Pagination from '@src/components/shared/pagination/Pagination';
import { userEvent } from '@testing-library/user-event';

describe('Pagination', () => {
  it('should Pagination component be created with first page selected', async () => {
    const pageSize = <Pagination page={0} total={5} pageSize={3} onPageChange={jest.fn()} />;

    // when
    const { getByRole } = renderWithProviders(pageSize);

    // then
    expect(getByRole('prev-page')).toBeInTheDocument();
    expect(getByRole('prev-page')).toHaveClass('page-nav-hidden');
    expect(getByRole('next-page')).toBeInTheDocument();
    expect(getByRole('next-page')).not.toHaveClass('page-nav-hidden');
    expect(getByRole('current-page').textContent).toEqual('1\xa0of\xa02');
  });

  it('should Pagination component be created with last page selected', async () => {
    const pageSize = <Pagination page={1} total={5} pageSize={3} onPageChange={jest.fn()} />;

    // when
    const { getByRole } = renderWithProviders(pageSize);

    // then
    expect(getByRole('prev-page')).toBeInTheDocument();
    expect(getByRole('prev-page')).not.toHaveClass('page-nav-hidden');
    expect(getByRole('next-page')).toBeInTheDocument();
    expect(getByRole('next-page')).toHaveClass('page-nav-hidden');
    expect(getByRole('current-page').textContent).toEqual('2\xa0of\xa02');
  });

  it('should change page to next one and invoke pageChange handler', async () => {
    const onPageChangeSpy = jest.fn();
    const pageSize = <Pagination page={0} total={5} pageSize={3} onPageChange={onPageChangeSpy} />;

    // when
    const { getByRole } = renderWithProviders(pageSize);

    // and
    expect(getByRole('prev-page')).toBeInTheDocument();
    expect(getByRole('next-page')).toBeInTheDocument();
    expect(getByRole('current-page').textContent).toEqual('1\xa0of\xa02');

    // and when
    await userEvent.click(getByRole('next-page-anchor'));

    // then
    expect(onPageChangeSpy).toHaveBeenCalledWith(1);
  });
});
