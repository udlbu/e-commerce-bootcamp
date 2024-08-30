export const validateConfirmPassword = (confirmPassword: string, password: string): boolean => {
  return confirmPassword === password;
};
