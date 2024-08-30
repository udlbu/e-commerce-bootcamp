import { validateBlank } from '@src/components/shared/validators/blank-validator';

describe('blank-validator', () => {
  ['', '  ', '    ', null, undefined].forEach((value) => {
    it(`should validate field: '${value}' as incorrect`, () => {
      // when
      const result = validateBlank(value);

      // then
      expect(result).toBeFalsy();
    });
  });

  ['x', ' c', '  e   ', 'dd'].forEach((value) => {
    it(`should validate field: '${value}' as correct`, () => {
      // when
      const result = validateBlank(value);

      // then
      expect(result).toBeTruthy();
    });
  });
});
