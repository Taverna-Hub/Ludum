export const AVAILABLE_TAGS = [
  "Aventura",
  "Ação",
  "RPG",
  "Puzzle",
  "Estratégia",
  "Simulação",
  "Esportes",
  "Corrida",
  "Terror",
  "Sobrevivência",
  "Indie",
  "Multiplayer",
  "Singleplayer",
  "Mundo Aberto",
  "Pixel Art",
  "2D",
  "3D",
  "Roguelike",
  "Metroidvania",
  "Plataforma",
  "Fantasia",
  "Ficção Científica",
  "Medieval",
  "Cyberpunk",
  "Pós-Apocalíptico",
  "Casual",
  "Competitivo",
  "História Rica",
  "Exploração",
  "Crafting",
] as const;


export type GameTag = typeof AVAILABLE_TAGS[number];


export function isValidTag(tag: string): tag is GameTag {
  return AVAILABLE_TAGS.includes(tag as GameTag);
}

export function filterValidTags(tags: string[]): GameTag[] {
  return tags.filter(isValidTag);
}
