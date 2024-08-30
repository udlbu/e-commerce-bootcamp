import { validateLastName } from '@src/components/register-page/validators/last-name-validator';

describe('last-name-validator', () => {
  ['', 'to', ' to ', '    ', 'bro', 'bro ', ' bro '].forEach((lastName) => {
    it(`should validate last name: '${lastName}' as incorrect`, () => {
      // when
      const result = validateLastName(lastName);

      // then
      expect(result).toBeFalsy();
    });
  });

  ['brow', 'brown ', '  brown   '].forEach((lastName) => {
    it(`should validate last name: '${lastName}' as correct`, () => {
      // when
      const result = validateLastName(lastName);

      // then
      expect(result).toBeTruthy();
    });
  });
});
