export interface ProductsState {
  data: Product[];
  total: number;
}

export interface AddProductRequest {
  name: string;
  ean: string;
  category: string;
  image: string | null;
  description: string | null;
}

export interface ModifyProductRequest {
  id: string;
  name: string;
  ean: string;
  category: string;
  image: string | null;
  description: string | null;
  version: number;
}

export interface Product {
  id: string;
  name: string;
  ean: string;
  category: ProductCategory;
  imageUrl: string | null;
  description: string | null;
  version: number;
}

export enum ProductCategory {
  ELECTRONICS = 'Electronics',
  CLOTHING = 'Clothing',
  HOME_APPLIANCES = 'Home Appliances',
  BEAUTY = 'Beauty',
  BOOKS = 'Books',
  SPORTS = 'Sports',
  FURNITURE = 'Furniture',
  TOYS = 'Toys',
  JEWELRY = 'Jewelry',
  HEALTH = 'Health',
}
