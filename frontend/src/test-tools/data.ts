import { Product, ProductCategory } from '../types/product';
import { UserState } from '@src/types/user';

export const product = {
  id: '1',
  name: 'Iphone',
  ean: '89712302',
  category: ProductCategory.ELECTRONICS,
  description: 'Iphone description',
  imageUrl: 'image.jpg',
  version: 1,
} as Product;

export const offer = {
  id: '1',
  userId: '1',
  status: 'INACTIVE',
  price: '9.99',
  quantity: '5',
  version: 1,
  product,
};

export const user = {
  id: '1000',
  email: 'tom@mail.com',
  firstName: 'Tom',
  lastName: 'Burns',
  taxId: null,
  version: 1,
  address: {
    id: '1',
    country: 'Iceland',
    city: 'Reykjavík',
    street: 'Very Nice',
    buildingNo: '10a',
    postalCode: '123-123',
    flatNo: null,
    version: 1,
  },
} as UserState;

export const cartItem = {
  id: '1',
  offerId: '1',
  quantity: 1,
  price: '9.99',
  productName: 'product-name',
  ean: '123456',
  category: ProductCategory.CLOTHING,
  imageUrl: 'image.jpg',
  description: 'description',
};
