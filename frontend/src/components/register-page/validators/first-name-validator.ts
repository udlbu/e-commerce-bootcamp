export const validateFirstName = (firstName: string): boolean => {
  if (!firstName || firstName.trim().length < 3) {
    return false;
  }
  return true;
};
