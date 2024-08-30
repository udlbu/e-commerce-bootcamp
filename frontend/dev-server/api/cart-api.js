const { offerList } = require('./offer-api');
const { products } = require('./products/index');
let cartsHolder = { carts: [] };
let seq = 1;

function register(router) {
  // ADD ITEM
  router.post('/carts', (ctx, next) => {
    if (!ctx.request.body.userId || !ctx.request.body.offerId || !ctx.request.body.quantity) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      let currentCart = cartsHolder.carts.find((cart) => {
        return Number(cart.userId) === Number(ctx.request.body.userId);
      });
      if (!currentCart) {
        const offer = findOffer(ctx.request.body.offerId);
        const product = findProduct(offer.productId);
        cartsHolder.carts.push({
          id: seq++,
          userId: ctx.request.body.userId,
          items: [
            {
              id: 1,
              offerId: ctx.request.body.offerId,
              quantity: ctx.request.body.quantity,
              price: offer.price,
              productName: product.name,
              ean: product.ean,
              category: product.category,
              imageUrl: product.imageUrl,
              description: product.description,
            },
          ],
        });
      } else {
        const currentItem = currentCart.items.find(
          (item) => Number(item.offerId) === Number(ctx.request.body.offerId),
        );
        if (!currentItem) {
          const offer = findOffer(ctx.request.body.offerId);
          const product = findProduct(offer.productId);
          currentCart.items.push({
            id: currentCart.items.length + 1,
            offerId: ctx.request.body.offerId,
            quantity: ctx.request.body.quantity,
            price: offer.price,
            productName: product.name,
            ean: product.ean,
            category: product.category,
            imageUrl: product.imageUrl,
            description: product.description,
          });
        } else {
          currentItem.quantity = currentItem.quantity + ctx.request.body.quantity;
        }
      }
      ctx.body = {};
      ctx.response.status = 200;
    }
    next();
  });

  router.get('/carts/:id', (ctx, next) => {
    ctx.body = cartsHolder.carts.find((cart) => Number(cart.userId) === Number(ctx.params.id));
    ctx.response.status = 200;
    next();
  });

  router.post('/carts/remove-item', (ctx, next) => {
    if (!ctx.request.body.userId || !ctx.request.body.offerId) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      cartsHolder.carts = cartsHolder.carts.map((cart) => {
        if (Number(cart.userId) === ctx.request.body.userId) {
          cart.items = cart.items.filter(
            (item) => Number(item.offerId) !== Number(ctx.request.body.offerId),
          );
        }
        return cart;
      });
    }
    ctx.body = {};
    ctx.response.status = 200;
    next();
  });

  router.put('/carts/change-quantity', (ctx, next) => {
    if (!ctx.request.body.userId || !ctx.request.body.offerId || !ctx.request.body.quantity) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      let currentCart = cartsHolder.carts.find((cart) => {
        return Number(cart.userId) === Number(ctx.request.body.userId);
      });
      if (!currentCart) {
        ctx.response.status = 400;
        ctx.body = 'Cart not found';
      } else {
        const currentItem = currentCart.items.find(
          (item) => Number(item.offerId) === Number(ctx.request.body.offerId),
        );
        if (!currentItem) {
          ctx.response.status = 400;
          ctx.body = 'Item not found';
        } else {
          currentItem.quantity = ctx.request.body.quantity;
          ctx.body = {};
          ctx.response.status = 200;
        }
      }
    }
    next();
  });
}

function findOffer(offerId) {
  const offer = offerList.find((offer) => Number(offer.id) === Number(offerId));
  if (!offer) {
    throw Error('Offer must exist');
  }
  return offer;
}

function findProduct(productId) {
  const product = products.find((prod) => Number(prod.id) === Number(productId));
  if (!product) {
    throw Error('Product must exist');
  }
  return product;
}

function removeCart(cartId) {
  cartsHolder.carts = cartsHolder.carts.filter((it) => Number(it.id) !== Number(cartId));
}
module.exports = {
  register,
  cartsHolder,
  removeCart,
};
