import React from 'react';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { BrowserRouter } from 'react-router-dom';
import LoginPage from '@src/components/login-page/LoginPage';
import { act, fireEvent } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import { setupServer } from 'msw/node';
import { rest } from 'msw';

interface Credentials {
  username: string;
  password: string;
}

const authenticationRequests = [] as unknown as Credentials[];

const server = setupServer(
  rest.post('/api/authenticate', (req, res, ctx) => {
    return req.json().then((data) => {
      authenticationRequests.push(data);
      if (data.password === 'invalid') {
        return res(ctx.status(403), ctx.json({}));
      }
      return res(ctx.json({}));
    });
  }),

  rest.get(`/api/users/current`, (req, res, ctx) => {
    return res(ctx.json({}));
  }),
);

beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
  authenticationRequests.splice(0, authenticationRequests.length);
});
afterAll(() => server.close());
describe('LoginPage', () => {
  it('should LoginPage component be created', () => {
    // given
    const loginPage = (
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(loginPage);

    // then
    expect(getByRole('email')).toBeInTheDocument();
    expect(getByRole('password')).toBeInTheDocument();
    expect(getByRole('loginBtn')).toBeInTheDocument();
    expect(getByRole('loginBtn')).toBeEnabled();
    expect(getByRole('registerBtn')).toBeInTheDocument();
    expect(getByRole('registerBtn')).toBeEnabled();
  });

  it('should invoke authenticate endpoint when user enter email, password and press login button', async () => {
    // given
    const USERNAME = 'sample@mail.com';
    const PASSWORD = 's3cr3t';

    const loginPage = (
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(loginPage);

    // then
    expect(getByRole('loginBtn')).toBeEnabled();

    // and when
    fireEvent.change(getByRole('email'), { target: { value: USERNAME } });
    fireEvent.change(getByRole('password'), { target: { value: PASSWORD } });

    // then
    expect(getByRole('loginBtn')).toBeEnabled();

    // and when
    await act(async () => {
      await userEvent.click(getByRole('loginBtn'));
    });

    // then
    expect(authenticationRequests.length).toEqual(1);
    expect(authenticationRequests[0].username).toEqual(USERNAME);
    expect(authenticationRequests[0].password).toEqual(PASSWORD);
  });

  it('should display invalid email or password message when user enters invalid credentials', async () => {
    // given
    const USERNAME = 'sample@mail.com';
    const INVALID_PASSWORD = 'invalid';

    const loginPage = (
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(loginPage);
    fireEvent.change(getByRole('email'), { target: { value: USERNAME } });
    fireEvent.change(getByRole('password'), { target: { value: INVALID_PASSWORD } });
    await act(async () => {
      await userEvent.click(getByRole('loginBtn'));
    });

    // then
    expect(getByRole('loginError').textContent).toEqual('Invalid email or password');
  });

  it('should display field required when user touched but did not enter values for email or password', async () => {
    // given
    const loginPage = (
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(loginPage);
    fireEvent.change(getByRole('email'), { target: { value: 'sample@ma' } }); // start entering value
    fireEvent.change(getByRole('email'), { target: { value: '' } }); // then removed
    fireEvent.change(getByRole('password'), { target: { value: ' ' } });
    await act(async () => {
      await userEvent.click(getByRole('loginBtn'));
    });

    // then
    expect(getByRole('emailError').textContent).toEqual('Field is required');
    expect(getByRole('passwordError').textContent).toEqual('Field is required');
  });
});
