const { cdnPath } = require('./cdnPath');
const beauty = [
  {
    id: -1,
    name: 'RadiantGlow Serum',
    ean: '1234567890123',
    category: 'BEAUTY',
    imageUrl: cdnPath + 'beauty_1.jpg',
    description:
      'Achieve a luminous complexion with RadiantGlow Serum. Infused with natural antioxidants and hyaluronic acid, this serum deeply hydrates and revitalizes your skin, leaving it smooth, supple, and radiant.',
    version: 1,
  },
  {
    id: -2,
    name: 'LuxeLash Mascara',
    ean: '2345678901234',
    category: 'BEAUTY',
    imageUrl: cdnPath + 'beauty_2.jpg',
    description:
      'Get stunning, voluminous lashes with LuxeLash Mascara. Its innovative formula lengthens and thickens lashes while maintaining a lightweight feel. The precision brush ensures clump-free application.',
    version: 1,
  },
  {
    id: -3,
    name: 'SilkTouch Hair Oil',
    ean: '3456789012345',
    category: 'BEAUTY',
    imageUrl: cdnPath + 'beauty_3.jpg',
    description:
      'Transform your hair with SilkTouch Hair Oil. Enriched with nourishing oils and vitamins, this lightweight formula tames frizz, adds shine, and promotes healthier-looking hair without weighing it down.',
    version: 1,
  },
  {
    id: -4,
    name: 'CrystalBreeze Perfume',
    ean: '4567890123456',
    category: 'BEAUTY',
    imageUrl: cdnPath + 'beauty_4.jpg',
    description:
      "Embrace a captivating aura with CrystalBreeze Perfume. This exquisite fragrance combines floral notes with hints of citrus and musk, creating a harmonious and long-lasting scent that's perfect for any occasion.",
    version: 1,
  },
  {
    id: -5,
    name: 'DiamondDew Highlighter',
    ean: '5678901234567',
    category: 'BEAUTY',
    imageUrl: cdnPath + 'beauty_5.jpg',
    description:
      "Illuminate your features with DiamondDew Highlighter. Its finely milled pigments catch the light to deliver a dazzling, ethereal glow. Apply to cheekbones, brow bones, and cupid's bow for an instant luminous effect.",
    version: 1,
  },
];
module.exports = {
  beauty: beauty,
};
