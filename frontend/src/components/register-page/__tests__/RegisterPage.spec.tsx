import React from 'react';

import { render, fireEvent, act } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import RegisterPage from '@src/components/register-page/RegisterPage';
import { rest } from 'msw';
import { AddUserRequest } from '@src/types/user';
import { setupServer } from 'msw/node';

const registerButton = 'button[role="registerBtn"]';

const addUserRequests = [] as unknown as [AddUserRequest];
const server = setupServer(
  rest.post('/api/users', (req, res, ctx) => {
    req.json().then((data) => {
      addUserRequests.push(data);
    });
    return res(ctx.json({}));
  }),
);

beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
  addUserRequests.splice(0, addUserRequests.length);
});
afterAll(() => server.close());

describe('Register Page', () => {
  it('should RegisterPage component be created', () => {
    // given
    const registerPage = (
      <BrowserRouter>
        <RegisterPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = render(registerPage);

    // then
    expect(getByRole('firstName')).toBeInTheDocument();
    expect(getByRole('lastName')).toBeInTheDocument();
    expect(getByRole('password')).toBeInTheDocument();
    expect(getByRole('confirmPassword')).toBeInTheDocument();
    expect(getByRole('taxId')).toBeInTheDocument();
    expect(getByRole('country')).toBeInTheDocument();
    expect(getByRole('city')).toBeInTheDocument();
    expect(getByRole('street')).toBeInTheDocument();
    expect(getByRole('buildingNo')).toBeInTheDocument();
    expect(getByRole('flatNo')).toBeInTheDocument();
    expect(getByRole('postalCode')).toBeInTheDocument();
  });

  it('should call addUser REST api when "Register" button is clicked', async () => {
    // given
    const registerPage = (
      <BrowserRouter>
        <RegisterPage />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = render(registerPage);

    // then
    expect(getByRole('registerBtn')).toBeDisabled();

    // and when
    fireEvent.change(getByRole('firstName'), { target: { value: 'Phil' } });
    fireEvent.change(getByRole('lastName'), { target: { value: 'Connor' } });
    fireEvent.change(getByRole('email'), { target: { value: 'ph.connor@mail.com' } });
    fireEvent.change(getByRole('password'), { target: { value: 's3cr3T' } });
    fireEvent.change(getByRole('confirmPassword'), { target: { value: 's3cr3T' } });
    fireEvent.change(getByRole('taxId'), { target: { value: '89712302' } });
    fireEvent.change(getByRole('country'), { target: { value: 'England' } });
    fireEvent.change(getByRole('city'), { target: { value: 'London' } });
    fireEvent.change(getByRole('street'), { target: { value: 'St. Paul' } });
    fireEvent.change(getByRole('buildingNo'), { target: { value: '1a' } });
    fireEvent.change(getByRole('flatNo'), { target: { value: '10' } });
    fireEvent.change(getByRole('postalCode'), { target: { value: '10-A123' } });

    // then
    expect(getByRole('registerBtn')).not.toBeDisabled();

    // and when
    await act(async () => {
      await userEvent.click(container.querySelector(registerButton));
    });

    // then
    expect(addUserRequests.length).toEqual(1);
    expect(addUserRequests[0].firstName).toEqual('Phil');
    expect(addUserRequests[0].lastName).toEqual('Connor');
    expect(addUserRequests[0].email).toEqual('ph.connor@mail.com');
    expect(addUserRequests[0].password).toEqual('s3cr3T');
    expect(addUserRequests[0].taxId).toEqual('89712302');
    expect(addUserRequests[0].address.country).toEqual('England');
    expect(addUserRequests[0].address.city).toEqual('London');
    expect(addUserRequests[0].address.street).toEqual('St. Paul');
    expect(addUserRequests[0].address.buildingNo).toEqual('1a');
    expect(addUserRequests[0].address.flatNo).toEqual('10');
    expect(addUserRequests[0].address.postalCode).toEqual('10-A123');
  });
});
