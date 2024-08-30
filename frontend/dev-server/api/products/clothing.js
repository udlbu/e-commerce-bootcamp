const { cdnPath } = require('./cdnPath');
const clothing = [
  {
    id: -11,
    name: 'Classic Denim Jacket',
    ean: '1234567890123',
    category: 'CLOTHING',
    imageUrl: cdnPath + 'clothing_1.jpg',
    description:
      'A timeless wardrobe staple, this classic denim jacket features a rugged design with button-front closure, front pockets, and adjustable cuffs. Made from high-quality denim for durability and style.',
    version: 1,
  },
  {
    id: -12,
    name: 'Elegant Lace Evening Gown',
    ean: '2345678901234',
    category: 'CLOTHING',
    imageUrl: cdnPath + 'clothing_2.jpg',
    description:
      'Make a stunning entrance at any formal event with this elegant lace evening gown. The intricate lace detailing, flowing silhouette, and subtle train create a look of timeless sophistication.',
    version: 1,
  },
  {
    id: -13,
    name: 'ActiveTech Performance Leggings',
    ean: '3456789012345',
    category: 'CLOTHING',
    imageUrl: cdnPath + 'clothing_3.jpg',
    description:
      'Elevate your workout attire with these ActiveTech Performance Leggings. Crafted from moisture-wicking, breathable fabric, these leggings provide comfort and support during intense workouts.',
    version: 1,
  },
  {
    id: -14,
    name: 'CozyChic Knit Sweater',
    ean: '4567890123456',
    category: 'CLOTHING',
    imageUrl: cdnPath + 'clothing_4.jpg',
    description:
      'Experience ultimate coziness with the CozyChic Knit Sweater. This luxurious knitwear piece features a relaxed fit, ribbed cuffs, and a soft blend of materials for warmth and style.',
    version: 1,
  },
  {
    id: -15,
    name: 'UrbanExplorer Waterproof Parka',
    ean: '5678901234567',
    category: 'CLOTHING',
    imageUrl: cdnPath + 'clothing_5.jpg',
    description:
      'Stay dry and stylish in the UrbanExplorer Waterproof Parka. Designed for urban adventurers, this parka offers waterproof protection, a detachable hood, multiple pockets, and a modern, sleek aesthetic.',
    version: 1,
  },
];
module.exports = {
  clothing: clothing,
};
