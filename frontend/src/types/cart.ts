export interface CartState {
  id: string;
  userId: string;
  items: CartItem[];
}

export const countCartItems = (cart: CartState) => {
  if (!cart?.items || cart.items.length === 0) {
    return 0;
  }
  return cart.items.map((it) => it.quantity).reduce((prev, curr) => prev + curr);
};

export const totalCartValue = (cart: CartState) => {
  if (!cart?.items || cart.items.length === 0) {
    return 0;
  }
  return cart.items
    .map((it) => Number(it.quantity) * Number(it.price))
    .reduce((prev, curr) => prev + curr)
    .toFixed(2);
};

export interface CartItem {
  id: string;
  offerId: string;
  quantity: number;
  price: string;
  productName: string;
  ean: string;
  category: string;
  imageUrl: string;
  description: string;
}

export interface AddItemToCartRequest {
  userId: string;
  offerId: string;
  quantity: number;
}

export interface RemoveItemFromCartRequest {
  userId: string;
  offerId: string;
}

export interface ChangeItemQuantityRequest {
  userId: string;
  offerId: string;
  quantity: number;
}
