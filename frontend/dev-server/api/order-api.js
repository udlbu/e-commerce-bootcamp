const { cartsHolder, removeCart } = require('./cart-api');
let orders = [];
let seq = 1;

function register(router) {
  // create order
  router.post('/orders', (ctx, next) => {
    if (
      !ctx.request.body.cartId ||
      !ctx.request.body.deliveryMethod ||
      !ctx.request.body.paymentMethod
    ) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      const cart = cartsHolder.carts.find(
        (it) => Number(it.id) === Number(ctx.request.body.cartId),
      );
      const now = new Date();
      let lineNum = 1;
      const lines = cart.items.map((it) => {
        return {
          id: lineNum++,
          offerId: it.offerId,
          offerPrice: it.price,
          productName: it.productName,
          productEan: it.ean,
          productCategory: it.category,
          imageUrl: it.imageUrl,
          quantity: it.quantity,
          version: 1,
        };
      });
      orders.push({
        id: seq++,
        userId: cart.userId,
        deliveryMethod: ctx.request.body.deliveryMethod,
        deliveryStatus: 'DELIVERED',
        paymentMethod: ctx.request.body.paymentMethod,
        paymentStatus: 'SUCCESS',
        status: 'NEW',
        createdAt: now.toString(),
        updatedAt: now.toString(),
        version: 1,
        lines: lines,
      });
    }
    removeCart(ctx.request.body.cartId);
    ctx.body = {};
    ctx.response.status = 201;
    next();
  });

  // search orders
  router.post('/orders/search', (ctx, next) => {
    if (
      ctx.request.body.userId === undefined ||
      ctx.request.body.page === undefined ||
      ctx.request.body.pageSize === undefined
    ) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      const userOrders = orders.filter(
        (it) => Number(it.userId) === Number(ctx.request.body.userId),
      );
      ctx.response.status = 200;
      ctx.body = {
        orders: userOrders,
        total: userOrders.length,
      };
      next();
    }
  });
}

module.exports = {
  register,
};
