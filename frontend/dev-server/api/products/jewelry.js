const { cdnPath } = require('./cdnPath');
const jewelry = [
  {
    id: -41,
    name: 'Sparkling Solitaire Necklace',
    ean: '1234567890123',
    category: 'JEWELRY',
    imageUrl: cdnPath + 'jewelry_1.jpg',
    description:
      'Elevate your elegance with our Sparkling Solitaire Necklace. A dazzling round-cut diamond is delicately suspended on a fine white gold chain, creating a timeless piece that adds sophistication to any outfit.',
    version: 1,
  },
  {
    id: -42,
    name: 'Enchanted Gemstone Earrings',
    ean: '2345678901234',
    category: 'JEWELRY',
    imageUrl: cdnPath + 'jewelry_2.jpg',
    description:
      "Embrace enchantment with our Enchanted Gemstone Earrings. These exquisite earrings feature vibrant, multifaceted gemstones set in a swirling design of sterling silver, capturing the beauty of nature's colors and forms.",
    version: 1,
  },
  {
    id: -43,
    name: 'Regal Pearl Tiara',
    ean: '3456789012345',
    category: 'JEWELRY',
    imageUrl: cdnPath + 'jewelry_3.jpg',
    description:
      'Embody royalty with our Regal Pearl Tiara. Handcrafted with lustrous freshwater pearls and sparkling crystals, this tiara exudes elegance and charm, making it the perfect accessory for weddings and special occasions.',
    version: 1,
  },
  {
    id: -44,
    name: 'Celestial Constellation Bracelet',
    ean: '4567890123456',
    category: 'JEWELRY',
    imageUrl: cdnPath + 'jewelry_4.jpg',
    description:
      'Find your place in the cosmos with our Celestial Constellation Bracelet. Sterling silver links are adorned with intricately crafted zodiac constellations, creating a celestial piece that reflects the mysteries of the night sky.',
    version: 1,
  },
  {
    id: -45,
    name: 'Whimsical Flower Brooch',
    ean: '5678901234567',
    category: 'JEWELRY',
    imageUrl: cdnPath + 'jewelry_5.jpg',
    description:
      'Add a touch of whimsy with our Whimsical Flower Brooch. This intricately detailed brooch features delicate petals adorned with colorful enamel and a center of shimmering crystals, capturing the essence of a blooming garden.',
    version: 1,
  },
];
module.exports = {
  jewelry: jewelry,
};
