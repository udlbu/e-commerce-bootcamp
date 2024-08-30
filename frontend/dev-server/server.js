const Koa = require('koa');
const Router = require('koa-router');
const logger = require('koa-logger');
const app = new Koa();
const bodyParser = require('koa-bodyparser');
const userApi = require('./api/user-api');
const productApi = require('./api/product-api');
const offerApi = require('./api/offer-api');
const cartApi = require('./api/cart-api');
const orderApi = require('./api/order-api');
const { sessionToOAuthMiddleware } = require('./middleware/session-to-oauth-middleware');
const { authorizationMiddleware } = require('./middleware/authorization-middleware');

const router = new Router({
  prefix: '/api',
});

userApi.register(router);
productApi.register(router);
offerApi.register(router);
cartApi.register(router);
orderApi.register(router);

app.use(logger());
app.use(bodyParser());
app.use(sessionToOAuthMiddleware());
app.use(authorizationMiddleware());
app.use(router.routes());

app.listen(3000, () => console.log('Server started...'));
