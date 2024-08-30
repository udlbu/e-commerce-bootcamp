import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { fireEvent } from '@testing-library/react';
import PageSize from '@src/components/shared/page-size/PageSize';

describe('PageSize', () => {
  it('should PageSize component be created', async () => {
    const onPageChangeSpy = jest.fn();
    const pageSize = <PageSize pageSize={3} onPageSizeChange={onPageChangeSpy} />;

    // when
    const { getByRole, getAllByRole } = renderWithProviders(pageSize);

    // then
    expect(getByRole('page-size-label')).toBeInTheDocument();
    expect(getByRole('page-size-value')).toBeInTheDocument();
    expect(getByRole('page-size-label').textContent).toEqual('Show:\xa0');
    const options = getAllByRole('page-size-option');
    expect(options.length).toEqual(7);
    expect((options[0] as HTMLOptionElement).selected).toBeTruthy();
    expect((options[1] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[2] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[3] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[4] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[5] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[6] as HTMLOptionElement).selected).toBeFalsy();
  });

  it('should change pageSize value', async () => {
    const onPageChangeSpy = jest.fn();
    const pageSize = <PageSize pageSize={3} onPageSizeChange={onPageChangeSpy} />;
    const newPageSize = 90;

    // when
    const { getByRole, getAllByRole } = renderWithProviders(pageSize);
    fireEvent.change(getByRole('page-size-value'), { target: { value: newPageSize } });

    // then
    expect(getByRole('page-size-label')).toBeInTheDocument();
    expect(getByRole('page-size-value')).toBeInTheDocument();
    expect(getByRole('page-size-label').textContent).toEqual('Show:\xa0');
    const options = getAllByRole('page-size-option');
    expect(options.length).toEqual(7);
    expect((options[0] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[1] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[2] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[3] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[4] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[5] as HTMLOptionElement).selected).toBeFalsy();
    expect((options[6] as HTMLOptionElement).selected).toBeTruthy();
    expect(onPageChangeSpy).toBeCalledWith(newPageSize);
  });
});
