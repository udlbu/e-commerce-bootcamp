import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import AuthWrapperRoute, {
  AccessType,
} from '@src/components/shared/auth-wrapper-route/AuthWrapperRoute';
import { user } from '@src/test-tools/data';
import { screen } from '@testing-library/react';

describe('AuthWrapperRoute', () => {
  it('should protected component be accessible only if user is logged in', () => {
    // given
    const authWrapper = (
      <BrowserRouter>
        <AuthWrapperRoute accessType={AccessType.AUTH_REQUIRED}>
          <div role='component'>protected component</div>
        </AuthWrapperRoute>
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(authWrapper, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('component')).toBeInTheDocument();
    expect(getByRole('component').textContent).toEqual('protected component');
  });

  it('should not protected component be accessible when user is not logged in', () => {
    // given
    const authWrapper = (
      <BrowserRouter>
        <AuthWrapperRoute accessType={AccessType.AUTH_REQUIRED}>
          <div role='component'>protected component</div>
        </AuthWrapperRoute>
      </BrowserRouter>
    );

    // when
    renderWithProviders(authWrapper);

    // then
    expect(screen.queryByRole('component')).toBeNull();
  });

  it('should component be accessible only if user is not logged in and access type is AUTH_NOT_ALLOWED', () => {
    // given
    const authWrapper = (
      <BrowserRouter>
        <AuthWrapperRoute accessType={AccessType.AUTH_NOT_ALLOWED}>
          <div role='component'>some component visible only for unauthenticated users</div>
        </AuthWrapperRoute>
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(authWrapper);

    // then
    expect(getByRole('component')).toBeInTheDocument();
    expect(getByRole('component').textContent).toEqual(
      'some component visible only for unauthenticated users',
    );
  });

  it('should not component be accessible when user is logged in and access type is AUTH_NOT_ALLOWED', () => {
    // given
    const authWrapper = (
      <BrowserRouter>
        <AuthWrapperRoute accessType={AccessType.AUTH_NOT_ALLOWED}>
          <div role='component'>some component visible only for unauthenticated users</div>
        </AuthWrapperRoute>
      </BrowserRouter>
    );

    // when
    renderWithProviders(authWrapper, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(screen.queryByRole('component')).toBeNull();
  });
});
