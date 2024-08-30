import { validatePrice } from '@src/components/shared/validators/price-validator';

describe('price-validator', () => {
  [
    '',
    '-1',
    '0.0',
    null,
    undefined,
    'dd',
    ' d ',
    '0',
    '1.001',
    '10.0001',
    '1,',
    '1,1',
    '1,01',
  ].forEach((value) => {
    it(`should validate field: '${value}' as incorrect`, () => {
      // when
      const result = validatePrice(value);

      // then
      expect(result).toBeFalsy();
    });
  });

  ['1', ' 1.01', '10', '10.1', '0.1', '10.', '1.00', '1.0'].forEach((value) => {
    it(`should validate field: '${value}' as correct`, () => {
      // when
      const result = validatePrice(value);

      // then
      expect(result).toBeTruthy();
    });
  });
});
