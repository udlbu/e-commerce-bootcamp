let { electronics } = require('./electronics.js');
const { clothing } = require('./clothing');
const { homeAppliances } = require('./homeAppliances');
const { beauty } = require('./beauty');
const { books } = require('./books');
const { sports } = require('./sports');
const { furniture } = require('./furniture');
const { toys } = require('./toys');
const { jewelry } = require('./jewelry');
const { health } = require('./health');
module.exports = {
  products: [
    ...beauty,
    ...books,
    ...clothing,
    ...electronics,
    ...furniture,
    ...health,
    ...homeAppliances,
    ...jewelry,
    ...sports,
    ...toys,
  ],
};
