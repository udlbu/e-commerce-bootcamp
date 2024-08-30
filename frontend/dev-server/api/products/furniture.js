const { cdnPath } = require('./cdnPath');
const furniture = [
  {
    id: -26,
    name: 'LuxeVelvet Sofa',
    ean: '1234567890123',
    category: 'FURNITURE',
    imageUrl: cdnPath + 'furniture_1.jpg',
    description:
      'The LuxeVelvet Sofa combines modern design with ultimate comfort. Upholstered in luxurious velvet fabric, this sofa features deep cushioning and sleek metal legs. Its elegant profile makes it a perfect addition to any contemporary living space.',
    version: 1,
  },
  {
    id: -27,
    name: 'RusticOak Dining Table',
    ean: '2345678901234',
    category: 'FURNITURE',
    imageUrl: cdnPath + 'furniture_2.jpg',
    description:
      "The RusticOak Dining Table exudes charm and durability. Crafted from solid oak, this table showcases a distressed finish that highlights its natural grain patterns. With its spacious surface and sturdy construction, it's an ideal choice for gatherings and family meals.",
    version: 1,
  },
  {
    id: -28,
    name: 'ZenHaven Bedframe',
    ean: '3456789012345',
    category: 'FURNITURE',
    imageUrl: cdnPath + 'furniture_3.jpg',
    description:
      'Create a serene bedroom retreat with the ZenHaven Bedframe. Crafted from sustainable bamboo, this minimalist bedframe offers clean lines and a calming presence. Its low-profile design and earthy tones contribute to a harmonious sleeping environment.',
    version: 1,
  },
  {
    id: -29,
    name: 'UrbanEdge Bookshelf',
    ean: '4567890123456',
    category: 'FURNITURE',
    imageUrl: cdnPath + 'furniture_4.jpg',
    description:
      'The UrbanEdge Bookshelf merges urban aesthetics with functional storage. Made from a combination of metal and reclaimed wood, this bookshelf features open shelving for displaying books, decor, and more. Its industrial design adds a touch of edgy style to any room.',
    version: 1,
  },
  {
    id: -30,
    name: 'CoastalBreeze Lounge Chair',
    ean: '5678901234567',
    category: 'FURNITURE',
    imageUrl: cdnPath + 'furniture_5.jpg',
    description:
      'Unwind by the sea or bring the coastal vibes home with the CoastalBreeze Lounge Chair. Crafted with weather-resistant wicker, this chair offers relaxed seating for outdoor or indoor spaces. Its ergonomic design and plush cushions make it the perfect spot for relaxation.',
    version: 1,
  },
];
module.exports = {
  furniture: furniture,
};
