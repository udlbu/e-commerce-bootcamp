import React from 'react';
import { useAppSelector } from '@src/store/hooks';
import { Navigate } from 'react-router-dom';

export enum AccessType {
  AUTH_REQUIRED,
  AUTH_NOT_ALLOWED,
}

interface AuthWrapperRouteProps {
  accessType: AccessType;
  children: string | React.JSX.Element | React.JSX.Element[];
}

const AuthWrapperRoute: React.FC<AuthWrapperRouteProps> = ({ children, accessType }) => {
  const isUserLoggedIn = (): boolean => {
    const user = useAppSelector((state) => state.user);
    return !!user;
  };

  if (accessType === AccessType.AUTH_REQUIRED && !isUserLoggedIn()) {
    return <Navigate to='/login' replace />;
  }
  if (accessType === AccessType.AUTH_NOT_ALLOWED && isUserLoggedIn()) {
    return <Navigate to='/' replace />;
  }
  return <>{children}</>;
};

export default AuthWrapperRoute;
