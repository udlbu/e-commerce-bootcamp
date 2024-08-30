const { getTokenBySession } = require('../api/domain/session-repository');

function sessionToOAuthMiddleware() {
  return async (ctx, next) => {
    const sessionId = ctx.cookies.get('x-session-id');
    const token = getTokenBySession(sessionId);
    if (token) {
      ctx.header['Authorization'] = `Bearer ${token}`;
    }
    await next();
  };
}

module.exports = {
  sessionToOAuthMiddleware,
};
