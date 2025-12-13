export interface Game {
  id: string;
  title: string;
  slug: string;
  description: string;
  price: number;
  originalPrice?: number;
  coverImage: string;
  screenshots: string[];
  tags: string[];
  developerId: string;
  developerName: string;
  rating: number;
  reviewCount: number;
  releaseDate: string;
  isEarlyAccess: boolean;
  hasAdultContent: boolean;
  modsEnabled: boolean;
  downloadCount: number;
}

export interface CrowdfundingCampaign {
  id: string;
  gameId: string;
  title: string;
  description: string;
  coverImage: string;
  goal: number;
  currentAmount: number;
  backers: number;
  daysLeft: number;
  rewards: CampaignReward[];
}

export interface CampaignReward {
  id: string;
  amount: number;
  title: string;
  description: string;
  backers: number;
}

export interface Mod {
  id: string;
  gameId: string;
  gameName: string;
  title: string;
  description: string;
  coverImage: string;
  creatorId: string;
  creatorName: string;
  rating: number;
  downloads: number;
  version: string;
  lastUpdate: string;
  subscribers: number;
}

export interface User {
  id: string;
  name: string;
  type: 'player' | 'developer';
  avatar: string;
  bio: string;
  followers: number;
  following: number;
  gamesPublished?: number;
  gamesPlayed?: number;
}


// Mock Games
export const mockGames: Game[] = [
  {
    id: 'jogo-1',
    title: 'Cyber Knights',
    slug: 'cyber-knights',
    description: 'Um RPG de ação futurista onde você é um mercenário cybernético em uma megacidade distópica. Explore um mundo aberto, complete missões e customize seu personagem com implantes cibernéticos.',
    price: 59.90,
    originalPrice: 79.90,
    coverImage: 'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=800',
      'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800',
      'https://images.unsplash.com/photo-1552820728-8b83bb6b2b0b?w=800',
    ],
    tags: ['RPG', 'Ação', 'Cyberpunk', 'Mundo Aberto'],
    developerId: 'dev1',
    developerName: 'NeonStudios',
    rating: 4.8,
    reviewCount: 1247,
    releaseDate: '2024-03-15',
    isEarlyAccess: false,
    hasAdultContent: false,
    modsEnabled: true,
    downloadCount: 45000,
  },
  {
    id: 'jogo-2',
    title: 'Dungeon Survivors',
    slug: 'dungeon-survivors',
    description: 'Um roguelike brutal onde cada run é única. Enfrente hordas de monstros, colete itens poderosos e tente sobreviver até o chefe final. Morte é apenas o começo!',
    price: 29.90,
    coverImage: 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1551103782-8ab07afd45c1?w=800',
      'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800',
    ],
    tags: ['Roguelike', 'Ação', 'Indie', 'Difícil'],
    developerId: 'dev2',
    developerName: 'PixelForge',
    rating: 4.6,
    reviewCount: 892,
    releaseDate: '2024-06-20',
    isEarlyAccess: true,
    hasAdultContent: false,
    modsEnabled: true,
    downloadCount: 28000,
  },
  {
    id: 'jogo-3',
    title: 'Starship Commander',
    slug: 'starship-commander',
    description: 'Comande sua própria frota espacial neste jogo de estratégia épico. Construa naves, gerencie recursos e conquiste a galáxia através de diplomacia ou força bruta.',
    price: 0,
    coverImage: 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800',
      'https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=800',
    ],
    tags: ['Estratégia', 'Espacial', 'Grátis', '4X'],
    developerId: 'dev1',
    developerName: 'NeonStudios',
    rating: 4.3,
    reviewCount: 2341,
    releaseDate: '2024-01-10',
    isEarlyAccess: false,
    hasAdultContent: false,
    modsEnabled: false,
    downloadCount: 120000,
  },
  {
    id: 'jogo-4',
    title: 'Mystic Garden',
    slug: 'mystic-garden',
    description: 'Um jogo relaxante de simulação de jardinagem com elementos mágicos. Cultive plantas místicas, descubra criaturas fantásticas e crie seu próprio jardim dos sonhos.',
    price: 19.90,
    coverImage: 'https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=800',
      'https://images.unsplash.com/photo-1518531933037-91b2f5f229cc?w=800',
    ],
    tags: ['Simulação', 'Relaxante', 'Indie', 'Fantasia'],
    developerId: 'dev2',
    developerName: 'PixelForge',
    rating: 4.9,
    reviewCount: 567,
    releaseDate: '2024-08-05',
    isEarlyAccess: false,
    hasAdultContent: false,
    modsEnabled: true,
    downloadCount: 15000,
  },
];

// Mock Crowdfunding
export const mockCampaigns: CrowdfundingCampaign[] = [
  {
    id: 'c1',
    gameId: 'g7',
    title: 'Echoes of Eternity',
    description: 'Um RPG narrativo ambientado em um universo de fantasia dark. Decisões impactam profundamente a história e o destino dos personagens.',
    coverImage: 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800',
    goal: 50000,
    currentAmount: 42350,
    backers: 847,
    daysLeft: 12,
    rewards: [
      {
        id: 'rw1',
        amount: 25,
        title: 'Apoiador',
        description: 'Cópia digital do jogo + créditos',
        backers: 423,
      },
      {
        id: 'rw2',
        amount: 50,
        title: 'Entusiasta',
        description: 'Jogo + Soundtrack + Artbook digital',
        backers: 312,
      },
      {
        id: 'rw3',
        amount: 100,
        title: 'Lendário',
        description: 'Tudo anterior + Nome nos créditos + Item exclusivo in-game',
        backers: 112,
      },
    ],
  },
  {
    id: 'c2',
    gameId: 'g8',
    title: 'Pixel Survivors',
    description: 'Jogo de sobrevivência em pixel art com crafting profundo e mundo gerado proceduralmente. Construa, explore e sobreviva!',
    coverImage: 'https://images.unsplash.com/photo-1556438064-2d7646166914?w=800',
    goal: 30000,
    currentAmount: 38420,
    backers: 1203,
    daysLeft: 5,
    rewards: [
      {
        id: 'rw4',
        amount: 15,
        title: 'Early Bird',
        description: 'Cópia do jogo com desconto',
        backers: 856,
      },
      {
        id: 'rw5',
        amount: 30,
        title: 'Crafters',
        description: 'Jogo + Acesso beta + Skin exclusiva',
        backers: 347,
      },
    ],
  },
];

// Mock Mods
export const mockMods: Mod[] = [
  {
    id: 'm1',
    gameId: 'jogo-1',
    gameName: 'Cyber Knights',
    title: 'Enhanced Graphics Pack',
    description: 'Melhora drasticamente os gráficos do jogo com texturas em 4K, iluminação aprimorada e efeitos visuais redesenhados.',
    coverImage: 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800',
    creatorId: 'u4',
    creatorName: 'ModMaster',
    rating: 4.9,
    downloads: 12450,
    version: '2.1.0',
    lastUpdate: '2024-09-15',
    subscribers: 8234,
  },
  {
    id: 'm2',
    gameId: 'jogo-1',
    gameName: 'Cyber Knights',
    title: 'New Weapons Arsenal',
    description: 'Adiciona 50+ novas armas balanceadas ao jogo, desde pistolas futuristas até rifles de plasma devastadores.',
    coverImage: 'https://images.unsplash.com/photo-1595433707802-6b2626ef1c91?w=800',
    creatorId: 'u5',
    creatorName: 'GunSmith',
    rating: 4.7,
    downloads: 9821,
    version: '1.5.2',
    lastUpdate: '2024-10-01',
    subscribers: 6543,
  },
  {
    id: 'm3',
    gameId: 'jogo-2',
    gameName: 'Dungeon Survivors',
    title: 'Ultimate Classes Overhaul',
    description: 'Rebalanceia todas as classes e adiciona 10 novas classes jogáveis com mecânicas únicas.',
    coverImage: 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800',
    creatorId: 'u6',
    creatorName: 'ClassCrafter',
    rating: 4.8,
    downloads: 7234,
    version: '3.0.1',
    lastUpdate: '2024-09-28',
    subscribers: 5432,
  },
];

// Mock Posts
export const mockPosts: Post[] = [
  {
    id: 'p1',
    gameId: 'jogo-1',
    gameName: 'Cyber Knights',
    userId: 'u1',
    userName: 'João Silva',
    content: 'Acabei de zerar a história principal! Que final incrível! Alguém mais conseguiu o final secreto? 🚀',
    image: 'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=600',
    date: '2024-10-12',
    likes: 234,
    comments: 45,
  },
  {
    id: 'p2',
    gameId: 'jogo-3',
    gameName: 'Starship Commander',
    userId: 'u7',
    userName: 'Ana Paula',
    content: 'Minha frota está ficando épica! Quem quiser formar aliança, me chama. Estamos recrutando pilotos experientes! 🛸',
    date: '2024-10-11',
    likes: 156,
    comments: 28,
  },
  {
    id: 'p3',
    gameId: 'jogo-2',
    gameName: 'Dungeon Survivors',
    userId: 'u3',
    userName: 'Pedro Santos',
    content: 'Build absurda de mago que descobri! Consegui chegar até o andar 50 pela primeira vez. Vou fazer um guia completo em breve.',
    image: 'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600',
    date: '2024-10-10',
    likes: 892,
    comments: 123,
  },
];

// Mock Users
export const mockUsers: User[] = [
  {
    id: 'u1',
    name: 'João Silva',
    type: 'player',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=joao',
    bio: 'Jogador apaixonado por RPGs e jogos indie. Sempre em busca da próxima aventura épica!',
    followers: 1234,
    following: 456,
    gamesPlayed: 87,
  },
  {
    id: 'dev1',
    name: 'NeonStudios',
    type: 'developer',
    avatar: 'https://api.dicebear.com/7.x/initials/svg?seed=NS',
    bio: 'Estúdio indie focado em criar experiências cyberpunk imersivas. Criadores de Cyber Knights e Starship Commander.',
    followers: 12400,
    following: 89,
    gamesPublished: 2,
  },
  {
    id: 'dev2',
    name: 'PixelForge',
    type: 'developer',
    avatar: 'https://api.dicebear.com/7.x/initials/svg?seed=PF',
    bio: 'Desenvolvemos jogos pixel art com mecânicas profundas. Amamos roguelikes e desafios!',
    followers: 8900,
    following: 134,
    gamesPublished: 2,
  },
];

// Current user mock (para simular usuário logado)
export const mockCurrentUser: User = {
  id: 'current',
  name: 'Você',
  type: 'player',
  avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=current',
  bio: 'Explorando o mundo dos jogos indie!',
  followers: 234,
  following: 89,
  gamesPlayed: 23,
};

// Biblioteca do usuário atual (jogos que ele "comprou")
export const mockUserLibrary = ['jogo-1', 'jogo-2', 'jogo-4'];

// Carteira
export const mockWallet = {
  availableBalance: 127.50,
  blockedBalance: 50.00,
  transactions: [
    {
      id: 't1',
      type: 'purchase',
      description: 'Compra: Cyber Knights',
      amount: -59.90,
      date: '2024-10-10',
      status: 'completed',
    },
    {
      id: 't2',
      type: 'deposit',
      description: 'Adição de saldo',
      amount: 200.00,
      date: '2024-10-08',
      status: 'completed',
    },
    {
      id: 't3',
      type: 'crowdfunding',
      description: 'Apoio: Echoes of Eternity',
      amount: -50.00,
      date: '2024-10-05',
      status: 'pending',
    },
    {
      id: 't4',
      type: 'refund',
      description: 'Reembolso: Shadow Tactics',
      amount: 44.90,
      date: '2024-10-03',
      status: 'completed',
    },
  ],
};
