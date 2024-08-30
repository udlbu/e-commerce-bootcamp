import React from 'react';
import { act, fireEvent } from '@testing-library/react';
import { userEvent } from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import { rest } from 'msw';
import { ModifyUserRequest } from '@src/types/user';
import { setupServer } from 'msw/node';
import EditUserPage from '@src/components/edit-user-page/EditUserPage';
import { renderWithProviders } from '@src/test-tools/test-utils';
import { user } from '@src/test-tools/data';

const saveButton = 'button[role="save-btn"]';

const modifyUserRequests = [] as unknown as [ModifyUserRequest];
const server = setupServer(
  rest.put('/api/users', (req, res, ctx) => {
    req.json().then((data) => {
      modifyUserRequests.push(data);
    });
    return res(ctx.json({}));
  }),
  rest.get('/api/users/current', (req, res, ctx) => {
    return res(ctx.json(user));
  }),
);

beforeAll(() => server.listen());
afterEach(() => {
  server.resetHandlers();
  modifyUserRequests.splice(0, modifyUserRequests.length);
});
afterAll(() => server.close());

describe('Edit User Page', () => {
  it('should EditUserPage component be created', () => {
    // given
    const editUserPage = (
      <BrowserRouter>
        <EditUserPage />
      </BrowserRouter>
    );

    // when
    const { getByRole } = renderWithProviders(editUserPage, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('edit-user-title').textContent).toEqual('Hello, Tom\xa0Burns (tom@mail.com)');
    expect(getByRole('tax-id')).toHaveAttribute('value', '');
    expect(getByRole('country')).toHaveAttribute('value', 'Iceland');
    expect(getByRole('city')).toHaveAttribute('value', 'Reykjavík');
    expect(getByRole('street')).toHaveAttribute('value', 'Very Nice');
    expect(getByRole('building-no')).toHaveAttribute('value', '10a');
    expect(getByRole('flat-no')).toHaveAttribute('value', '');
    expect(getByRole('postal-code')).toHaveAttribute('value', '123-123');
  });

  it('should send modifyUser request when "Save" button is clicked', async () => {
    // given
    const editUserPage = (
      <BrowserRouter>
        <EditUserPage />
      </BrowserRouter>
    );

    // when
    const { container, getByRole } = renderWithProviders(editUserPage, {
      preloadedState: {
        user: {
          ...user,
        },
      },
    });

    // then
    expect(getByRole('save-btn')).toBeInTheDocument();

    // and when
    fireEvent.change(getByRole('tax-id'), { target: { value: '89712302' } });
    fireEvent.change(getByRole('country'), { target: { value: 'England' } });
    fireEvent.change(getByRole('city'), { target: { value: 'London' } });
    fireEvent.change(getByRole('street'), { target: { value: 'St. Paul' } });
    fireEvent.change(getByRole('building-no'), { target: { value: '1a' } });
    fireEvent.change(getByRole('flat-no'), { target: { value: '10' } });
    fireEvent.change(getByRole('postal-code'), { target: { value: '10-A123' } });

    // then
    expect(getByRole('save-btn')).not.toBeDisabled();

    // and when
    await act(async () => {
      await userEvent.click(container.querySelector(saveButton));
    });

    // then
    expect(modifyUserRequests.length).toEqual(1);
    expect(modifyUserRequests[0].id).toEqual('1000');
    expect(modifyUserRequests[0].firstName).toEqual('Tom');
    expect(modifyUserRequests[0].lastName).toEqual('Burns');
    expect(modifyUserRequests[0].email).toEqual('tom@mail.com');
    expect(modifyUserRequests[0].taxId).toEqual('89712302');
    expect(modifyUserRequests[0].version).toEqual(1);
    expect(modifyUserRequests[0].address.id).toEqual('1');
    expect(modifyUserRequests[0].address.country).toEqual('England');
    expect(modifyUserRequests[0].address.city).toEqual('London');
    expect(modifyUserRequests[0].address.street).toEqual('St. Paul');
    expect(modifyUserRequests[0].address.buildingNo).toEqual('1a');
    expect(modifyUserRequests[0].address.flatNo).toEqual('10');
    expect(modifyUserRequests[0].address.postalCode).toEqual('10-A123');
    expect(modifyUserRequests[0].address.version).toEqual(1);
  });
});
