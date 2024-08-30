export interface UserState {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  taxId: string | null;
  country: string;
  version: number;
  address: UserAddress;
}

export interface UserAddress {
  id: string;
  country: string;
  city: string;
  street: string;
  buildingNo: string;
  flatNo: string | null;
  postalCode: string;
  version: number;
}

export interface AddUserRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  taxId: string | null;
  address: AddUserAddress;
}

export interface AddUserAddress {
  country: string;
  city: string;
  street: string;
  buildingNo: string;
  flatNo: string | null;
  postalCode: string;
}

export interface ModifyUserRequest {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  taxId: string | null;
  version: number;
  address: ModifyUserAddress;
}

export interface ModifyUserAddress {
  id: string;
  country: string;
  city: string;
  street: string;
  buildingNo: string;
  flatNo: string | null;
  postalCode: string;
  version: number;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}
