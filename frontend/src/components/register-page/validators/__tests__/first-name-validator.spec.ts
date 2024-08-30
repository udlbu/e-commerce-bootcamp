import { validateFirstName } from '@src/components/register-page/validators/first-name-validator';

describe('first-name-validator', () => {
  ['', 'to', ' to ', '    '].forEach((firstName) => {
    it(`should validate first name: '${firstName}' as incorrect`, () => {
      // when
      const result = validateFirstName(firstName);

      // then
      expect(result).toBeFalsy();
    });
  });

  ['tom', 'ann', '  tom   ', ' ann'].forEach((firstName) => {
    it(`should validate first name: '${firstName}' as correct`, () => {
      // when
      const result = validateFirstName(firstName);

      // then
      expect(result).toBeTruthy();
    });
  });
});
