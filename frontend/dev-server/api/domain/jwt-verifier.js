const axios = require('axios');
const { PUBLIC_KEY_ENDPOINT } = require('../keycloak');
const jwtToPem = require('jwk-to-pem');
const jwt = require('jsonwebtoken');

async function verifyJwt(bearer) {
  const token = bearer.slice(7);
  const jwks = await axios.get(PUBLIC_KEY_ENDPOINT);
  const decoded = jwt.decode(token, { complete: true });
  const kid = decoded.header.kid;
  const key = jwks.data.keys.find((key) => key.kid === kid);
  const publicKey = jwtToPem(key);
  try {
    jwt.verify(token, publicKey);
  } catch (ex) {
    console.error(JSON.stringify(ex));
  }
}

module.exports = {
  verifyJwt,
};
