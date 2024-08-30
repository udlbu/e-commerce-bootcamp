export interface OrdersState {
  data: Order[];
  total: number;
}

export interface Order {
  id: string;
  deliveryMethod: DeliveryMethod;
  deliveryStatus: string;
  paymentMethod: PaymentMethod;
  paymentStatus: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  version: number;
  lines: OrderLine[];
}

export interface OrderLine {
  id: number;
  offerId: string;
  offerPrice: number;
  productId: string;
  productName: string;
  productEan: string;
  productCategory: string;
  imageUrl: string;
  quantity: number;
  version: number;
}

export interface SubmitOrderRequest {
  cartId: string;
  deliveryMethod: string;
  paymentMethod: string;
}

export interface SearchOrdersRequest {
  userId: string;
  page: number;
  pageSize: number;
}

export enum DeliveryMethod {
  DHL,
  INPOST,
  DPD,
  FEDEX,
}

export enum PaymentMethod {
  CARD,
  TRANSFER,
}

export const totalOrderValue = (order: Order) => {
  if (!order?.lines || order.lines.length === 0) {
    return 0;
  }
  return order.lines
    .map((it) => Number(it.quantity) * Number(it.offerPrice))
    .reduce((prev, curr) => prev + curr)
    .toFixed(2);
};
