import { Product } from '@src/types/product';

export interface OffersState {
  data: Offer[];
  total: number;
}

export interface OfferDetailsState {
  offer: Offer;
}
export interface AddOfferRequest {
  userId: string;
  price: string;
  quantity: string;
  productId: string;
}

export interface SearchOffersRequest {
  userId?: string;
  productCategory?: string;
  page: number;
  pageSize: number;
}

export interface ModifyOfferRequest {
  offerId: string;
  userId: string;
  version: number;
  price?: string;
  quantity?: string;
  productId?: string;
}
export interface Offer {
  id: string;
  userId: string;
  status: string;
  price: string;
  quantity: string;
  version: number;
  product: Product;
}
