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

export interface Review {
  id: string;
  gameId: string;
  userId: string;
  userName: string;
  rating: number;
  comment: string;
  recommended: boolean;
  date: string;
  updatedAt?: string;
  helpful: number;
  deleted?: boolean;
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

export interface Post {
  id: string;
  gameId: string;
  gameName: string;
  userId: string;
  userName: string;
  content: string;
  image?: string;
  date: string;
  likes: number;
  comments: number;
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
    id: '1',
    title: 'Cyber Knights',
    slug: 'cyber-knights',
    description: 'Um RPG cyberpunk de mundo aberto onde suas escolhas moldam o futuro da cidade. Explore Nova Tokyo, uma metrópole futurista cheia de perigos e oportunidades.',
    price: 59.90,
    coverImage: 'https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=1200',
      'https://images.unsplash.com/photo-1542751371-adc38448a05e?w=1200',
    ],
    tags: ['RPG', 'Cyberpunk', 'Mundo Aberto', 'História Rica'],
    developerId: 'dev1',
    developerName: 'NeonStudios',
    rating: 4.8,
    reviewCount: 1247,
    releaseDate: '2024-03-15',
    isEarlyAccess: false,
    hasAdultContent: false,
    modsEnabled: true,
    downloadCount: 12400,
  },
  {
    id: '2',
    title: 'Dungeon Survivors',
    slug: 'dungeon-survivors',
    description: 'Roguelike desafiador com combate tático por turnos. Cada partida é única com masmorras geradas proceduralmente e centenas de combinações de builds.',
    price: 0,
    originalPrice: 39.90,
    coverImage: 'https://images.unsplash.com/photo-1551103782-8ab07afd45c1?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1551103782-8ab07afd45c1?w=1200',
    ],
    tags: ['Roguelike', 'Estratégia', 'Indie', 'Difícil'],
    developerId: 'dev2',
    developerName: 'PixelForge',
    rating: 4.5,
    reviewCount: 856,
    releaseDate: '2024-01-20',
    isEarlyAccess: false,
    hasAdultContent: false,
    modsEnabled: true,
    downloadCount: 8900,
  },
  {
    id: '3',
    title: 'Starship Commander',
    slug: 'starship-commander',
    description: 'Simulador espacial onde você comanda sua própria nave. Explore galáxias, faça comércio, forme alianças ou torne-se um pirata temido.',
    price: 89.90,
    coverImage: 'https://images.unsplash.com/photo-1614732414444-096e5f1122d5?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1614732414444-096e5f1122d5?w=1200',
    ],
    tags: ['Simulação', 'Espacial', 'Multiplayer', 'Sandbox'],
    developerId: 'dev1',
    developerName: 'NeonStudios',
    rating: 4.9,
    reviewCount: 2103,
    releaseDate: '2023-11-10',
    isEarlyAccess: false,
    hasAdultContent: false,
    modsEnabled: true,
    downloadCount: 25600,
  },
  {
    id: '4',
    title: 'Mystic Realms',
    slug: 'mystic-realms',
    description: 'MMORPG de fantasia com foco em narrativa e escolhas morais. Junte-se a guildas, participe de raids épicas e molde o destino do reino.',
    price: 0,
    coverImage: 'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1552820728-8b83bb6b773f?w=1200',
    ],
    tags: ['MMORPG', 'Fantasia', 'Cooperativo', 'PvP'],
    developerId: 'dev3',
    developerName: 'MysticGames',
    rating: 4.3,
    reviewCount: 3421,
    releaseDate: '2024-02-01',
    isEarlyAccess: true,
    hasAdultContent: false,
    modsEnabled: false,
    downloadCount: 45200,
  },
  {
    id: '5',
    title: 'Shadow Tactics',
    slug: 'shadow-tactics',
    description: 'Jogo stealth tático ambientado no Japão feudal. Controle uma equipe de ninjas com habilidades únicas e complete missões de infiltração.',
    price: 44.90,
    coverImage: 'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=1200',
    ],
    tags: ['Stealth', 'Estratégia', 'História', 'Tático'],
    developerId: 'dev2',
    developerName: 'PixelForge',
    rating: 4.7,
    reviewCount: 1567,
    releaseDate: '2024-04-05',
    isEarlyAccess: false,
    hasAdultContent: false,
    modsEnabled: true,
    downloadCount: 15800,
  },
  {
    id: '6',
    title: 'Neon Racing',
    slug: 'neon-racing',
    description: 'Corrida arcade futurista com física arcade e visuais neon vibrantes. Compete online ou desafie seus amigos em pistas impossíveis.',
    price: 29.90,
    coverImage: 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=800',
    screenshots: [
      'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=1200',
    ],
    tags: ['Corrida', 'Arcade', 'Multiplayer', 'Casual'],
    developerId: 'dev4',
    developerName: 'SpeedStudios',
    rating: 4.4,
    reviewCount: 892,
    releaseDate: '2024-05-20',
    isEarlyAccess: false,
    hasAdultContent: false,
    modsEnabled: false,
    downloadCount: 9200,
  },
];

// Mock Reviews
export const mockReviews: Review[] = [
  {
    id: 'r1',
    gameId: '1',
    userId: 'u1',
    userName: 'João Silva',
    rating: 5,
    comment: 'Jogo incrível! A história é envolvente e o mundo aberto é repleto de detalhes. Já joguei mais de 100 horas e ainda descobrindo coisas novas. Vale cada centavo!',
    recommended: true,
    date: '2024-10-01',
    helpful: 234,
  },
  {
    id: 'r2',
    gameId: '1',
    userId: 'u2',
    userName: 'Maria Costa',
    rating: 4,
    comment: 'Muito bom, mas tem alguns bugs que precisam ser corrigidos. A jogabilidade é excelente e os gráficos são de tirar o fôlego.',
    recommended: true,
    date: '2024-09-28',
    helpful: 156,
  },
  {
    id: 'r3',
    gameId: '2',
    userId: 'u3',
    userName: 'Pedro Santos',
    rating: 5,
    comment: 'Melhor roguelike que já joguei! A dificuldade é perfeitamente balanceada e cada run é única. Já viciei completamente.',
    recommended: true,
    date: '2024-10-05',
    helpful: 89,
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
    gameId: '1',
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
    gameId: '1',
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
    gameId: '2',
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
    gameId: '1',
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
    gameId: '3',
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
    gameId: '2',
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
export const mockUserLibrary = ['1', '2', '4'];

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
