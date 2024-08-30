import React from 'react';
import { act, fireEvent } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import { rest } from 'msw';
import { ChangePasswordRequest } from '@src/types/user';
import { setupServer } from 'msw/node';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { user } from '@src/test-tools/data';
import ChangePasswordPage from '@src/components/change-password-page/ChangePasswordPage';

const changeRequests = [] as unknown as [ChangePasswordRequest];
let logoutRequests = 0;
const server = setupServer(
  rest.put('/api/users/:id/change-password', (req, res, ctx) => {
    req.json().then((data) => {
      changeRequests.push(data);
    });
    return res(ctx.json({}));
  }),

  rest.post('/api/logout', (req, res, ctx) => {
    logoutRequests++;
    return res(ctx.json({}));
  }),
);

beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
  changeRequests.splice(0, changeRequests.length);
  logoutRequests = 0;
});
afterAll(() => server.close());

describe('Change Password Page', () => {
  it('should ChangePasswordPage component be created', () => {
    // given
    const changePasswordPage = (
      <BrowserRouter>
        <ChangePasswordPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(changePasswordPage, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('old-password')).toBeInTheDocument();
    expect(getByRole('new-password')).toBeInTheDocument();
    expect(getByRole('confirm-password')).toBeInTheDocument();
    expect(getByRole('change-password-btn')).toBeInTheDocument();
    expect(getByRole('change-password-btn')).toBeDisabled();
  });

  it('should send changePassword request when "Change" button is clicked', async () => {
    // given
    const OLD_PASSWORD = 's3cr3t';
    const NEW_PASSWORD = 'secret';
    const changePasswordPage = (
      <BrowserRouter>
        <ChangePasswordPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(changePasswordPage, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('change-password-btn')).toBeDisabled();

    // and when
    fireEvent.change(getByRole('old-password'), { target: { value: OLD_PASSWORD } });
    fireEvent.change(getByRole('new-password'), { target: { value: NEW_PASSWORD } });
    fireEvent.change(getByRole('confirm-password'), { target: { value: NEW_PASSWORD } });

    // then
    expect(getByRole('change-password-btn')).toBeEnabled();

    // and when
    await act(async () => {
      await userEvent.click(getByRole('change-password-btn'));
    });

    // then
    expect(changeRequests.length).toEqual(1);
    expect(logoutRequests).toEqual(1);
    expect(changeRequests[0].oldPassword).toEqual(OLD_PASSWORD);
    expect(changeRequests[0].newPassword).toEqual(NEW_PASSWORD);
  });
});
