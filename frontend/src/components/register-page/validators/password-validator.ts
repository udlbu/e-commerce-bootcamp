export const validatePassword = (password: string): boolean => {
  if (!password || password.indexOf(' ') != -1 || password.length < 5) {
    return false;
  }
  return true;
};
