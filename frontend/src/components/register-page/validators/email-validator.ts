export const validateEmail = (email: string): boolean => {
  if (!email || email.trim().length < 5) {
    return false;
  }
  const emailExp = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i;
  return emailExp.test(email.trim());
};
