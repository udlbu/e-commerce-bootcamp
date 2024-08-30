const crypto = require('crypto');
const sessions = {};

function getTokenBySession(sessionId) {
  return sessions[sessionId];
}

function onAuthentication(token) {
  const sessionId = crypto.randomUUID();
  sessions[sessionId] = token;
  return sessionId;
}

function onLogout(sessionId) {
  sessions[sessionId] = undefined;
}

module.exports = {
  getTokenBySession,
  onAuthentication,
  onLogout,
};
