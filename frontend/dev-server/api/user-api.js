const axios = require('axios');
const { onAuthentication, onLogout } = require('./domain/session-repository');
const { TOKEN_ENDPOINT } = require('./keycloak');
const jwt = require('jsonwebtoken');
let seq = 1;

let users = [
  {
    id: seq++,
    email: 'seller@mail.com',
    firstName: 'Johnny',
    lastName: 'Seller',
    taxId: '346674322',
    version: 1,
    address: {
      id: 1,
      country: 'England',
      city: 'London',
      street: 'Downing street',
      buildingNo: '32a',
      flatNo: '10',
      postalCode: 'AX 223-1',
      version: 1,
    },
  },
  {
    id: seq++,
    email: 'buyer@mail.com',
    firstName: 'Maria',
    lastName: 'Buyer',
    taxId: '554982201',
    version: 1,
    address: {
      id: 1,
      country: 'England',
      city: 'London',
      street: 'Downing street',
      buildingNo: '12',
      flatNo: '9a',
      postalCode: 'AX 223-2',
      version: 1,
    },
  },
];

function register(router) {
  // ADD USER
  router.post('/users', (ctx, next) => {
    if (
      !ctx.request.body.email ||
      !ctx.request.body.password ||
      !ctx.request.body.firstName ||
      !ctx.request.body.lastName ||
      !ctx.request.body.address.country ||
      !ctx.request.body.address.city ||
      !ctx.request.body.address.street ||
      !ctx.request.body.address.buildingNo ||
      !ctx.request.body.address.postalCode
    ) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      users.push({
        id: seq++,
        email: ctx.request.body.email,
        password: ctx.request.body.password,
        firstName: ctx.request.body.firstName,
        lastName: ctx.request.body.lastName,
        taxId: ctx.request.body.taxId,
        version: 1,
        address: {
          id: 1,
          country: ctx.request.body.country,
          city: ctx.request.body.city,
          street: ctx.request.body.street,
          buildingNo: ctx.request.body.buildingNo,
          flatNo: ctx.request.body.flatNo,
          postalCode: ctx.request.body.postalCode,
          version: 1,
        },
      });
      ctx.body = {};
      ctx.response.status = 201;
    }
    next();
  });

  // MODIFY USER
  router.put('/users', (ctx, next) => {
    if (
      !ctx.request.body.id ||
      !ctx.request.body.email ||
      !ctx.request.body.firstName ||
      !ctx.request.body.lastName ||
      !ctx.request.body.address ||
      !ctx.request.body.address.country ||
      !ctx.request.body.address.city ||
      !ctx.request.body.address.street ||
      !ctx.request.body.address.buildingNo ||
      !ctx.request.body.address.postalCode
    ) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      users = users.map((it) => {
        if (Number(it.id) === Number(ctx.request.body.id)) {
          return {
            ...it,
            taxId: ctx.request.body.taxId,
            address: {
              ...it.address,
              country: ctx.request.body.address.country,
              city: ctx.request.body.address.city,
              street: ctx.request.body.address.street,
              buildingNo: ctx.request.body.address.buildingNo,
              flatNo: ctx.request.body.address.flatNo,
              postalCode: ctx.request.body.address.postalCode,
            },
          };
        }
        return it;
      });
      ctx.body = {};
      ctx.response.status = 200;
    }
    next();
  });

  // POST AUTHENTICATE
  router.post('/authenticate', async (ctx, next) => {
    try {
      const res = await axios.post(
        TOKEN_ENDPOINT,
        `client_id=ecommerce-app-id&username=${ctx.request.body.username}&password=${ctx.request.body.password}&grant_type=password`,
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
        },
      );
      const sessionId = onAuthentication(res.data.access_token);
      ctx.cookies.set('x-session-id', sessionId, { httpOnly: true, maxAge: 3600000, path: '/' }); // 60 minutes
      let current = findUser((user) => user.email === ctx.request.body.username);
      if (current) {
        ctx.response.status = 200;
        ctx.body = {};
      } else {
        ctx.response.status = 404;
        ctx.body = 'User Not Found';
      }
    } catch (ex) {
      ctx.response.status = 401;
      ctx.body = 'Unauthorized';
    } finally {
      next();
    }
  });

  // POST logout
  router.post('/logout', async (ctx, next) => {
    onLogout(ctx.cookies.get('x-session-id'));
    ctx.response.status = 200;
    ctx.body = {};
    next();
  });

  // GET CURRENT USER
  router.get('/users/current', async (ctx, next) => {
    const bearer = ctx.request.header['Authorization'];
    if (bearer) {
      const token = bearer.slice(7);
      const { email } = jwt.decode(token);
      let current = findUser((user) => user.email === email);
      if (current) {
        ctx.response.status = 200;
        ctx.body = current;
      } else {
        ctx.response.status = 404;
        ctx.body = 'User Not Found';
      }
    } else {
      ctx.response.status = 401;
      ctx.body = 'Unauthorized';
    }
    next();
  });

  router.put('/users/:id/change-password', (ctx, next) => {
    if (!ctx.request.body.oldPassword || !ctx.request.body.newPassword) {
      ctx.response.status = 400;
      ctx.body = 'Please enter the data';
    } else {
      if (ctx.request.body.newPassword === 'notmatch') {
        ctx.body = {
          errors: [
            {
              code: 'PASSWORD_DOES_NOT_MATCH',
              message: 'Password does not match',
              path: null,
            },
          ],
        };
        ctx.response.status = 422;
      } else {
        // skip changing password in Keycloak
        ctx.body = {};
        ctx.response.status = 200;
      }
    }
    next();
  });
}

function findUser(predicate) {
  let current = users.filter((user) => {
    if (predicate(user)) {
      return true;
    }
  });
  if (current.length) {
    return current[0];
  }
  return null;
}

module.exports = {
  register,
};
