import { validateConfirmPassword } from '@src/components/register-page/validators/confirm-password-validator';

describe('confirm-password-validator', () => {
  [
    { password: '', confirmPassword: '', result: true },
    { password: 'a', confirmPassword: 'a', result: true },
    { password: 'Ab', confirmPassword: 'Ab', result: true },
    { password: 'b', confirmPassword: 'B', result: false },
    { password: 'B', confirmPassword: 'B ', result: false },
    { password: ' B', confirmPassword: 'B ', result: false },
    { password: ' B', confirmPassword: ' B ', result: false },
  ].forEach((data) => {
    it(`should validate passwords ${JSON.stringify(data)}`, () => {
      // when
      const result = validateConfirmPassword(data.confirmPassword, data.password);

      // then
      expect(result).toEqual(data.result);
    });
  });
});
