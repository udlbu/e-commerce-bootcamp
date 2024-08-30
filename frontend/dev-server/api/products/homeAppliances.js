const { cdnPath } = require('./cdnPath');
const homeAppliances = [
  {
    id: -36,
    name: 'TurboBlend Pro 5000',
    ean: '9781234567001',
    category: 'HOME_APPLIANCES',
    imageUrl: cdnPath + 'ha_1.jpg',
    description:
      'The TurboBlend Pro 5000 is a high-performance blender designed to effortlessly blend smoothies, soups, and more. With its powerful motor and advanced blade technology, you can achieve smooth and consistent results every time.  Product Name: SteamMist Deluxe Iron',
    version: 1,
  },
  {
    id: -37,
    name: 'SteamMist Deluxe Iron',
    ean: '9781234567002',
    category: 'HOME_APPLIANCES',
    imageUrl: cdnPath + 'ha_2.jpg',
    description:
      'The SteamMist Deluxe Iron makes ironing a breeze with its powerful steam output and precision control settings. Say goodbye to stubborn wrinkles as the steam penetrates fabric for a flawless finish.',
    version: 1,
  },
  {
    id: -38,
    name: 'ChillMaster 3000 Refrigerator',
    ean: '9781234567003',
    category: 'HOME_APPLIANCES',
    imageUrl: cdnPath + 'ha_3.jpg',
    description:
      'The ChillMaster 3000 Refrigerator offers ample storage space and advanced temperature control for keeping your groceries and beverages perfectly chilled. Its modern design and energy-efficient features make it a must-have for any kitchen.',
    version: 1,
  },
  {
    id: -39,
    name: 'EcoDry Pro 8000 Dryer',
    ean: '9781234567004',
    category: 'HOME_APPLIANCES',
    imageUrl: cdnPath + 'ha_4.jpg',
    description:
      'The EcoDry Pro 8000 Dryer combines cutting-edge drying technology with eco-friendly features. Its sensor-driven drying cycles optimize efficiency while protecting your clothes from over-drying, extending their lifespan.',
    version: 1,
  },
  {
    id: -40,
    name: 'ComfortZone Thermostat',
    ean: '9781234567005',
    category: 'HOME_APPLIANCES',
    imageUrl: cdnPath + 'ha_5.jpg',
    description:
      "The ComfortZone Thermostat gives you precise control over your home's temperature, providing comfort and energy savings. With programmable scheduling and intuitive interface, you can create the perfect ambiance at any time of the day.",
    version: 1,
  },
];
module.exports = {
  homeAppliances: homeAppliances,
};
