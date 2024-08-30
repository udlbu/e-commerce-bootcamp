export const validatePrice = (value: string): boolean => {
  if (!value || isNaN(Number(value))) {
    return false;
  }
  if (Number(value) <= 0) {
    return false;
  }
  if (value.indexOf('.') == -1) {
    return true;
  }
  return value.split('.')[1].length <= 2;
};
