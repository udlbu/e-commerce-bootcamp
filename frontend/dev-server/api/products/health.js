const { cdnPath } = require('./cdnPath');
const health = [
  {
    id: -31,
    name: 'VitaBoost Pro',
    ean: '1234567890123',
    category: 'HEALTH',
    imageUrl: cdnPath + 'health_1.jpg',
    description:
      'VitaBoost Pro is a powerful multivitamin supplement designed to support overall health and vitality. Packed with essential vitamins and minerals, it helps boost your immune system, enhance energy levels, and promote overall well-being.',
    version: 1,
  },
  {
    id: -32,
    name: 'FlexiJoint Plus',
    ean: '2345678901234',
    category: 'HEALTH',
    imageUrl: cdnPath + 'health_2.jpg',
    description:
      'FlexiJoint Plus is a joint support formula formulated to maintain healthy joints and promote flexibility. Its blend of natural ingredients helps reduce discomfort, supports joint mobility, and improves overall joint health.',
    version: 1,
  },
  {
    id: -33,
    name: 'ZenSleep Aide',
    ean: '3456789012345',
    category: 'HEALTH',
    imageUrl: cdnPath + 'health_3.jpg',
    description:
      'ZenSleep Aide is a natural sleep supplement that promotes restful sleep and relaxation. With a unique blend of calming herbs and nutrients, it helps alleviate stress, improve sleep quality, and wake up feeling refreshed.',
    version: 1,
  },
  {
    id: -34,
    name: 'DigestiPro Enzymes',
    ean: '4567890123456',
    category: 'HEALTH',
    imageUrl: cdnPath + 'health_4.jpg',
    description:
      'DigestiPro Enzymes is a digestive enzyme complex that aids in proper digestion and nutrient absorption. It supports a healthy gut by breaking down proteins, fats, and carbohydrates, helping to alleviate digestive discomfort.',
    version: 1,
  },
  {
    id: -35,
    name: 'PureGlow Collagen',
    ean: '5678901234567',
    category: 'HEALTH',
    imageUrl: cdnPath + 'health_5.jpg',
    description:
      'PureGlow Collagen is a beauty supplement designed to promote healthy skin, hair, and nails. It contains premium collagen peptides that support skin elasticity, reduce wrinkles, and strengthen hair and nails for a radiant appearance.',
    version: 1,
  },
];
module.exports = {
  health: health,
};
