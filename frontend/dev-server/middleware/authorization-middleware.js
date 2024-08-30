const { verifyJwt } = require('../api/domain/jwt-verifier');

function authorizationMiddleware() {
  return async (ctx, next) => {
    function isAuthenticateRequest() {
      return ctx.request.method === 'POST' && ctx.request.path === '/api/authenticate';
    }

    function isAddUserRequest() {
      return ctx.request.method === 'POST' && ctx.request.path === '/api/users';
    }

    function isSearchOffersRequest() {
      return ctx.request.method === 'POST' && ctx.request.path === '/api/offers/search';
    }

    function isGetOfferDetailsRequest() {
      return ctx.request.method === 'GET' && ctx.request.path.startsWith('/api/offers');
    }

    if (
      isAuthenticateRequest() ||
      isAddUserRequest() ||
      isSearchOffersRequest() ||
      isGetOfferDetailsRequest()
    ) {
      console.log(`[Public] request: ${ctx.request.method}:${ctx.request.path}`);
      await next();
      return;
    }

    console.log(`[Protected] request: ${ctx.request.method}:${ctx.request.path}`);
    if (!ctx.request.header['Authorization']) {
      console.log(
        `Invalid token: [${ctx.request.header['Authorization']}] [${ctx.request.method}:${ctx.request.path}]`,
      );
      ctx.response.status = 401;
      ctx.body = 'Unauthorized';
      return;
    }
    await verifyJwt(ctx.request.header['Authorization']);
    await next();
  };
}

module.exports = {
  authorizationMiddleware,
};
