const { cdnPath } = require('./cdnPath');
const toys = [
  {
    id: -51,
    name: 'FlutterPals',
    ean: '1234567890123',
    category: 'TOYS',
    imageUrl: cdnPath + 'toys_1.jpg',
    description:
      'FlutterPals are adorable interactive plush toys designed to mimic the movements and sounds of real birds. These soft and huggable companions chirp, tweet, and flap their wings, delighting children with their lifelike behavior.',
    version: 1,
  },
  {
    id: -52,
    name: 'RoboBuilder Set',
    ean: '2345678901234',
    category: 'TOYS',
    imageUrl: cdnPath + 'toys_2.jpg',
    description:
      'The RoboBuilder Set is an advanced building kit that allows kids to construct and program their own robots. With modular components and a user-friendly coding interface, children can create robots that walk, dance, and even perform tasks using sensors and motors.',
    version: 1,
  },
  {
    id: -53,
    name: 'AquaAdventures Playset',
    ean: '3456789012345',
    category: 'TOYS',
    imageUrl: cdnPath + 'toys_3.jpg',
    description:
      'Dive into imaginative underwater fun with the AquaAdventures Playset! This interactive water playset features a cascading waterfall, spinning whirlpool, and floating animal figurines. Kids can explore aquatic adventures and develop fine motor skills through water-based play.',
    version: 1,
  },
  {
    id: -54,
    name: 'Galactic Explorers Spaceship',
    ean: '4567890123456',
    category: 'TOYS',
    imageUrl: cdnPath + 'toys_4.jpg',
    description:
      'Blast off into the cosmos with the Galactic Explorers Spaceship! This futuristic playset includes a highly detailed spaceship, poseable astronaut figures, and detachable space vehicles. With flashing lights, sound effects, and a retractable landing gear, kids can embark on interstellar missions.',
    version: 1,
  },
  {
    id: -55,
    name: 'PuzzleBots Challenge',
    ean: '5678901234567',
    category: 'TOYS',
    imageUrl: cdnPath + 'toys_5.jpg',
    description:
      'Engage young minds with the PuzzleBots Challenge, a brain-teasing robotic puzzle game. Players must program their mini robots to navigate through mazes and obstacles to reach the target. The set includes a variety of challenge cards, encouraging critical thinking and coding skills.',
    version: 1,
  },
];
module.exports = {
  toys: toys,
};
