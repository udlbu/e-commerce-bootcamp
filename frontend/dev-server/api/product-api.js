let { products } = require('./products/index.js');
let seq = 1;

function register(router) {
  // ADD PRODUCT
  router.post('/products', (ctx, next) => {
    if (!ctx.request.body.name || !ctx.request.body.ean || !ctx.request.body.category) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      products.push({
        id: ++seq,
        name: ctx.request.body.name,
        ean: ctx.request.body.ean,
        category: ctx.request.body.category,
        // image: ctx.request.body.image, ignore base64 img
        imageUrl: 'http://localhost:8080/mock/image.png',
        description: ctx.request.body.description,
        version: 1,
      });
      ctx.body = {};
      ctx.response.status = 201;
    }
    next();
  });

  // UPDATE PRODUCT
  router.put('/products', (ctx, next) => {
    if (
      !ctx.request.body.id ||
      !ctx.request.body.name ||
      !ctx.request.body.ean ||
      !ctx.request.body.category ||
      !ctx.request.body.version
    ) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      const exists = products.some((prod) => Number(prod.id) === Number(ctx.request.body.id));
      if (!exists) {
        ctx.response.status = 404;
        ctx.body = 'Product Not Found';
      } else {
        products = products.map((prod) => {
          if (Number(prod.id) === Number(ctx.request.body.id)) {
            return {
              ...prod,
              name: ctx.request.body.name,
              ean: ctx.request.body.ean,
              category: ctx.request.body.category,
              // image: ctx.request.body.image, ignore base64 img
              description: ctx.request.body.description,
              version: prod.version + 1,
            };
          }
          return prod;
        });
        ctx.body = {};
        ctx.response.status = 200;
      }
    }
    next();
  });

  // GET PRODUCT
  router.get('/products/:id', (ctx, next) => {
    let current = products.filter((product) => {
      if (product.id === Number(ctx.params.id)) {
        return true;
      }
    });
    if (current.length) {
      ctx.body = current[0];
    } else {
      ctx.response.status = 404;
      ctx.body = 'User Not Found';
    }
    next();
  });

  // GET PRODUCTS
  router.get('/products', (ctx, next) => {
    const offset =
      (parseInt(ctx.request.query.page) || 0) * (parseInt(ctx.request.query.pageSize) || 10);
    const limit = parseInt(ctx.request.query.pageSize) || 10;
    ctx.body = {
      products: products.slice(offset, offset + limit),
      total: products.length,
    };
    next();
  });

  // DELETE PRODUCT
  router.delete('/products/:id', (ctx, next) => {
    const exists = products.some((prod) => prod.id === Number(ctx.params.id));
    if (!exists) {
      ctx.body = 'Product Not Found';
      ctx.response.status = 404;
    } else {
      products = products.filter((user) => user.id !== Number(ctx.params.id));
      ctx.response.status = 200;
      ctx.body = {};
    }
    next();
  });
}

module.exports = {
  register,
};
