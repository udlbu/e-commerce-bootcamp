import { validateEmail } from '@src/components/register-page/validators/email-validator';

describe('email-validator', () => {
  [
    '',
    'tom',
    'tom@',
    'tom.brown',
    'tom@brown',
    'tom@brown.',
    'tom@brown.  ',
    ' ',
    'tom  @brown.com',
  ].forEach((email) => {
    it(`should validate email: '${email}' as incorrect`, () => {
      // when
      const result = validateEmail(email);

      // then
      expect(result).toBeFalsy();
    });
  });

  ['tom@domain.com', 'tom.brown@mail.com', 'tom.brown@mail.com ', '  tom.brown@mail.com'].forEach(
    (email) => {
      it(`should validate email: '${email}' as correct`, () => {
        // when
        const result = validateEmail(email);

        // then
        expect(result).toBeTruthy();
      });
    },
  );
});
