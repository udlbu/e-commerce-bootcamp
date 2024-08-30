import { validatePassword } from '@src/components/register-page/validators/password-validator';

describe('password-validator', () => {
  ['', 'a3', ' 23a ', '    ', '23dR', '23dR ', ' 23dR '].forEach((password) => {
    it(`should validate password: '${password}' as incorrect`, () => {
      // when
      const result = validatePassword(password);

      // then
      expect(result).toBeFalsy();
    });
  });

  ['a2#4d', 'aaaaa', 'aaavb$5fdds'].forEach((password) => {
    it(`should validate password: '${password}' as correct`, () => {
      // when
      const result = validatePassword(password);

      // then
      expect(result).toBeTruthy();
    });
  });
});
