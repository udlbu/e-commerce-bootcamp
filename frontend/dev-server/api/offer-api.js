const { products } = require('./products/index');
const { offers } = require('./offers');
let offerList = offers;
let seq = 1;

function register(router) {
  // ADD OFFER
  router.post('/offers', (ctx, next) => {
    if (
      !ctx.request.body.userId ||
      !ctx.request.body.price ||
      !ctx.request.body.quantity ||
      !ctx.request.body.productId
    ) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      offerList.push({
        id: seq++,
        userId: ctx.request.body.userId,
        price: ctx.request.body.price,
        quantity: ctx.request.body.quantity,
        productId: ctx.request.body.productId,
        status: 'INACTIVE',
        version: 1,
      });
      ctx.body = {};
      ctx.response.status = 201;
    }
    next();
  });

  // UPDATE OFFER
  router.put('/offers', (ctx, next) => {
    if (
      !ctx.request.body.offerId ||
      !ctx.request.body.userId ||
      !ctx.request.body.quantity ||
      !ctx.request.body.price ||
      !ctx.request.body.productId ||
      !ctx.request.body.version
    ) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      const exists = offerList.some(
        (offer) => Number(offer.id) === Number(ctx.request.body.offerId),
      );
      if (!exists) {
        ctx.response.status = 404;
        ctx.body = 'Product Not Found';
      } else {
        offerList = offerList.map((offer) => {
          if (Number(offer.id) === Number(ctx.request.body.offerId)) {
            return {
              ...offer,
              userId: ctx.request.body.userId,
              price: ctx.request.body.price,
              quantity: ctx.request.body.quantity,
              productId: ctx.request.body.productId,
              version: offer.version + 1,
            };
          }
          return offer;
        });
        ctx.body = {};
        ctx.response.status = 200;
      }
    }
    next();
  });

  // GET OFFER
  router.get('/offers/:id', (ctx, next) => {
    let current = offerList.find((offer) => {
      return Number(offer.id) === Number(ctx.params.id);
    });
    if (current) {
      ctx.body = decorate(current);
    } else {
      ctx.response.status = 404;
      ctx.body = 'User Not Found';
    }
    next();
  });

  // DELETE OFFER
  router.delete('/offers/:id', (ctx, next) => {
    const exists = offerList.some((offer) => offer.id === Number(ctx.params.id));
    if (!exists) {
      ctx.body = 'Product Not Found';
      ctx.response.status = 404;
    } else {
      offerList = offerList.filter((user) => user.id !== Number(ctx.params.id));
      ctx.response.status = 200;
      ctx.body = {};
    }
    next();
  });

  // ACTIVATE OFFER
  router.put('/offers/:id/activate', (ctx, next) => {
    const exists = offerList.some((offer) => Number(offer.id) === Number(ctx.params.id));
    if (!exists) {
      ctx.body = 'Product Not Found';
      ctx.response.status = 404;
    } else {
      offerList = offerList.map((it) => {
        if (Number(it.id) === Number(ctx.params.id)) {
          return {
            ...it,
            status: 'ACTIVE',
          };
        }
        return it;
      });
      ctx.response.status = 200;
      ctx.body = {};
    }
    next();
  });

  // DEACTIVATE OFFER
  router.put('/offers/:id/deactivate', (ctx, next) => {
    const exists = offerList.some((offer) => Number(offer.id) === Number(ctx.params.id));
    if (!exists) {
      ctx.body = 'Product Not Found';
      ctx.response.status = 404;
    } else {
      offerList = offerList.map((it) => {
        if (Number(it.id) === Number(ctx.params.id)) {
          return {
            ...it,
            status: 'INACTIVE',
          };
        }
        return it;
      });
      ctx.response.status = 200;
      ctx.body = {};
    }
    next();
  });

  // SEARCH FOR ACTIVE OFFER PAGE
  router.post('/offers/search', (ctx, next) => {
    if (!ctx.request.body.page || !ctx.request.body.pageSize) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    }
    if (offerList.length === 0) {
      ctx.body = {
        offers: [],
        total: 0,
      };
      ctx.response.status = 200;
    } else {
      const offset = ctx.request.body.page * ctx.request.body.pageSize;
      const limit = ctx.request.body.pageSize;
      const offers = offerList
        .filter((it) => it.status === 'ACTIVE')
        .map((it) => decorate(it))
        .filter(
          (it) =>
            !ctx.request.body.productCategory ||
            it.product.category === ctx.request.body.productCategory,
        );
      ctx.body = {
        offers: offers.slice(offset, offset + limit),
        total: offers.length,
      };
      ctx.response.status = 200;
    }
    next();
  });

  // SEARCH FOR OFFER - for admin page
  router.get('/offers/user/search', (ctx, next) => {
    const offset =
      (parseInt(ctx.request.query.page) || 0) * (parseInt(ctx.request.query.pageSize) || 10);
    const limit = parseInt(ctx.request.query.pageSize) || 10;
    ctx.body = {
      offers: offerList.slice(offset, offset + limit).map((it) => decorate(it)),
      total: offerList.length,
    };
    next();
  });
}

const decorate = (offer) => {
  const product = products.find((prod) => Number(prod.id) === Number(offer.productId));
  return {
    id: offer.id,
    userId: offer.userId,
    status: offer.status,
    price: offer.price,
    quantity: offer.quantity,
    version: offer.version,
    product: product,
  };
};
module.exports = {
  register,
  offerList,
};
