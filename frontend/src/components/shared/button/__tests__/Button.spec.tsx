import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { fireEvent, screen } from '@testing-library/react';
import Button from '@src/components/shared/button/Button';

describe('Button', () => {
  it('should Button component be created in processing mode', async () => {
    // given
    const button = (
      <BrowserRouter>
        <Button
          role='test-button-role'
          processing={true}
          text='Text Button'
          onClick={jest.fn()}
          disabled={false}
        />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(button);

    // then
    expect(screen.queryByRole('test-button-role')).toBeNull();
    expect(getByRole('processing-icon')).toBeInTheDocument();
  });

  it('should Button component be created', async () => {
    // given
    const onClick = jest.fn();
    const button = (
      <BrowserRouter>
        <Button
          role='test-button-role'
          processing={false}
          text='Text Button'
          onClick={onClick}
          disabled={false}
        />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(button);

    // then
    expect(screen.queryByRole('processing-icon')).toBeNull();
    expect(getByRole('test-button-role')).toBeInTheDocument();

    // and when clicked
    fireEvent.click(getByRole('test-button-role'));

    // then
    expect(onClick).toBeCalledTimes(1);
  });
});
