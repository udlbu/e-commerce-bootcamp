const KEYCLOAK_HOST = 'http://localhost:38080/';
const PUBLIC_KEY_ENDPOINT = `${KEYCLOAK_HOST}realms/frontend-dev-realm/protocol/openid-connect/certs`;
const TOKEN_ENDPOINT = `${KEYCLOAK_HOST}realms/frontend-dev-realm/protocol/openid-connect/token`;

module.exports = {
  PUBLIC_KEY_ENDPOINT,
  TOKEN_ENDPOINT,
};
