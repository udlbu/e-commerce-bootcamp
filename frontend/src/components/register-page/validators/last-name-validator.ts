export const validateLastName = (lastName: string): boolean => {
  if (!lastName || lastName.trim().length < 4) {
    return false;
  }
  return true;
};
