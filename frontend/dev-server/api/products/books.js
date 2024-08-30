const { cdnPath } = require('./cdnPath');
const books = [
  {
    id: -6,
    name: '"Whispers of Eternity"',
    ean: '9781645678902',
    category: 'BOOKS',
    imageUrl: cdnPath + 'books_1.jpg',
    description:
      'In a world where time is fluid and destinies intertwine, "Whispers of Eternity" takes readers on a mesmerizing journey through love, loss, and the secrets that bind souls across generations. As the threads of fate unravel, characters grapple with their pasts and embrace the mysteries of a timeless connection that defies the bounds of mortality.',
    version: 1,
  },
  {
    id: -7,
    name: '"Chronicles of Lumina"',
    ean: '9781987654321',
    category: 'BOOKS',
    imageUrl: cdnPath + 'books_2.jpg',
    description:
      '"Chronicles of Lumina" transports readers to a realm of magic and wonder, where enchanted creatures roam and ancient prophecies hold the key to salvation. As darkness threatens to engulf the land, a reluctant hero emerges to lead a band of unlikely allies on a perilous quest that will determine the fate of Lumina and its inhabitants.',
    version: 1,
  },
  {
    id: -8,
    name: '"Echoes in the Mist"',
    ean: '9781556789012',
    category: 'BOOKS',
    imageUrl: cdnPath + 'books_3.jpg',
    description:
      'Set against the haunting backdrop of a mist-shrouded landscape, "Echoes in the Mist" delves into the intertwined lives of individuals haunted by their pasts. As they grapple with forgotten memories and suppressed emotions, they must navigate a labyrinth of intrigue and self-discovery to find solace and redemption.',
    version: 1,
  },
  {
    id: -9,
    name: '"Quantum Paradox"',
    ean: '9782365478901',
    category: 'BOOKS',
    imageUrl: cdnPath + 'books_4.jpg',
    description:
      '"Quantum Paradox" delves into the enigmatic world of quantum physics, where reality bends and the boundaries of science and philosophy blur. With a captivating blend of narrative and exploration, this book takes readers on a mind-bending journey through parallel universes, wave-particle duality, and the profound questions that challenge our understanding of existence.',
    version: 1,
  },
  {
    id: -10,
    name: '"Aurelia\'s Legacy"',
    ean: '9783124567890',
    category: 'BOOKS',
    imageUrl: cdnPath + 'books_5.jpg',
    description:
      'In a realm where magic and politics collide, "Aurelia\'s Legacy" follows the tumultuous journey of a young mage who discovers an ancient lineage and a hidden power that could reshape the fate of the kingdom. As alliances crumble and betrayals loom, she must navigate a treacherous path to claim her birthright and forge a new destiny.',
    version: 1,
  },
];
module.exports = {
  books: books,
};
