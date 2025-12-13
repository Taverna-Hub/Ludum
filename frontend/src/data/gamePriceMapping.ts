export interface GamePriceConfig {
  price: number;
  originalPrice?: number;
}

export type GamePriceMap = Record<string, number | GamePriceConfig>;

export const GAME_PRICE_MAPPING: GamePriceMap = {
  'enigma-do-medo': 49.9,
  'clair-obscur-expedition-33': { price: 159.2, originalPrice: 199.9 },
  'a-lenda-do-heroi-edicao-definitiva': 29.9,
  'hollow-knight': 46.99,
  'hollow-knight-silksong': 59.99,
  'super-adventure': 12.9,
  '457dc254-e1b0-4579-8ecf-d7bba4f0a88d': { price: 4.99, originalPrice: 19.9 },
  '9400ed87-6d28-4c41-8787-e1a9981dd79c': 134.9,
  'the-binding-of-isaac-rebirth': 27.99,
  'hades-ii': 88.99,
  'cult-of-the-lamb': { price: 32.47, originalPrice: 64.95 },
};

export function getGamePrice(slugOrId: string): GamePriceConfig | null {
  const mapping = GAME_PRICE_MAPPING[slugOrId];

  if (mapping === undefined) {
    return null;
  }

  if (typeof mapping === 'number') {
    return { price: mapping };
  }

  return mapping;
}

/**
 * Aplica o mapeamento de preço a um objeto de jogo
 * @param game - Objeto do jogo
 * @returns Jogo com preço aplicado (se houver override)
 */
export function applyPriceMapping<
  T extends { slug: string; id: string; price: number; originalPrice?: number },
>(game: T): T {
  // Tenta buscar por slug primeiro, depois por ID
  const priceConfig = getGamePrice(game.slug) || getGamePrice(game.id);

  if (!priceConfig) {
    return game;
  }

  return {
    ...game,
    price: priceConfig.price,
    originalPrice: priceConfig.originalPrice,
  };
}
