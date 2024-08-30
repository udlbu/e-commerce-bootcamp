const { cdnPath } = require('./cdnPath');
const sports = [
  {
    id: -46,
    name: 'PowerGrip Pro Fitness Gloves',
    ean: '1234567890123',
    category: 'SPORTS',
    imageUrl: cdnPath + 'sports_1.jpg',
    description:
      'Enhance your weightlifting and fitness routines with the PowerGrip Pro Fitness Gloves. These gloves provide superior grip and comfort, allowing you to lift with confidence and push your limits.',
    version: 1,
  },
  {
    id: -47,
    name: 'TrailBlaze UltraLight Hiking Backpack',
    ean: '2345678901234',
    category: 'SPORTS',
    imageUrl: cdnPath + 'sports_2.jpg',
    description:
      'The TrailBlaze UltraLight Hiking Backpack is your perfect companion for outdoor adventures. With its lightweight design and ergonomic features, it offers ample storage space and optimal comfort for extended hikes.',
    version: 1,
  },
  {
    id: -48,
    name: 'AquaFlex Pro Swim Goggles',
    ean: '3456789012345',
    category: 'SPORTS',
    imageUrl: cdnPath + 'sports_3.jpg',
    description:
      'Dive into the pool with the AquaFlex Pro Swim Goggles. Designed for serious swimmers, these goggles offer a comfortable fit, anti-fog lenses, and a sleek hydrodynamic profile to help you glide through the water effortlessly.',
    version: 1,
  },
  {
    id: -49,
    name: 'TurboDrive 5000 Indoor Cycling Bike',
    ean: '4567890123456',
    category: 'SPORTS',
    imageUrl: cdnPath + 'sports_4.jpg',
    description:
      'Experience the thrill of indoor cycling with the TurboDrive 5000. This state-of-the-art stationary bike offers customizable resistance levels, a digital console to track your workouts, and a comfortable saddle for long training sessions.',
    version: 1,
  },
  {
    id: -50,
    name: 'StrikeZone Precision Soccer Ball',
    ean: '5678901234567',
    category: 'SPORTS',
    imageUrl: cdnPath + 'sports_5.jpg',
    description:
      'Elevate your soccer skills with the StrikeZone Precision Soccer Ball. Engineered for accuracy and control, this ball features strategically placed panels for enhanced ball manipulation, making it ideal for training and improving your game.',
    version: 1,
  },
];
module.exports = {
  sports: sports,
};
