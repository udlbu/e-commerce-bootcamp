import userReducer, { addUser, logout } from './../userSlice';
import { UserState } from '@src/types/user';

describe('user reducer', () => {
  const initialState: UserState = null;
  it('should handle initial state', () => {
    expect(userReducer(undefined, { type: 'unknown' })).toEqual(null);
  });

  it('should handle addUser', () => {
    // given
    const user = {
      id: '1',
      firstName: 'Susan',
      lastName: 'Lee',
      email: 'susan.lee@mail.com',
      taxId: null,
      version: 1,
      address: {
        id: '1',
        country: 'England',
        city: 'London',
        street: 'Downing',
        buildingNo: '12a',
        flatNo: '77',
        postalCode: 'AW-332',
        version: 1,
      },
    } as UserState;

    // when
    const actual = userReducer(initialState, addUser(user));

    // then
    expect(actual).toEqual(user);
  });

  it('should handle logout', () => {
    // when
    const actual = userReducer(
      {
        id: '1',
        firstName: 'Susan',
        lastName: 'Lee',
        email: 'susan.lee@mail.com',
        taxId: null,
        version: 1,
        country: '',
        address: {
          id: '1',
          country: 'England',
          city: 'London',
          street: 'Downing',
          buildingNo: '12a',
          flatNo: '77',
          postalCode: 'AW-332',
          version: 1,
        },
      },
      logout(),
    );

    // then
    expect(actual).toEqual(null);
  });
});
