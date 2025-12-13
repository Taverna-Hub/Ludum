import { AVAILABLE_TAGS } from "@/constants/tags";

export const TAG_ID_TO_NAME: Record<string, string> = {
  "tag-tag-1": "Aventura",
  "tag-tag-2": "Ação",
  "tag-tag-3": "RPG",
  "tag-tag-4": "Puzzle",
  "tag-tag-5": "Estratégia",
  "tag-tag-6": "Simulação",
  "tag-tag-7": "Esportes",
  "tag-tag-8": "Corrida",
  "tag-tag-9": "Terror",
  "tag-tag-10": "Sobrevivência",
  "tag-tag-11": "Indie",
  "tag-tag-12": "Multiplayer",
  "tag-tag-13": "Singleplayer",
  "tag-tag-14": "Mundo Aberto",
  "tag-tag-15": "Pixel Art",
  "tag-tag-16": "2D",
  "tag-tag-17": "3D",
  "tag-tag-18": "Roguelike",
  "tag-tag-19": "Metroidvania",
  "tag-tag-20": "Plataforma",
  "tag-tag-21": "Fantasia",
  "tag-tag-22": "Ficção Científica",
  "tag-tag-23": "Medieval",
  "tag-tag-24": "Cyberpunk",
  "tag-tag-25": "Pós-Apocalíptico",
  "tag-tag-26": "Casual",
  "tag-tag-27": "Competitivo",
  "tag-tag-28": "História Rica",
  "tag-tag-29": "Exploração",
  "tag-tag-30": "Crafting",
};

export const TAG_NAME_TO_ID: Record<string, string> = Object.entries(
  TAG_ID_TO_NAME
).reduce((acc, [id, name]) => {
  acc[name] = id;
  return acc;
}, {} as Record<string, string>);

export function parseTagId(tagId: string): string {
  // Fazer case-insensitive para lidar com "Tag-tag-1" ou "tag-tag-1"
  return TAG_ID_TO_NAME[tagId] || TAG_ID_TO_NAME[tagId.toLowerCase()] || tagId;
}

export function parseTagIds(tagIds: string[]): string[] {
  return tagIds.map(parseTagId);
}

export function tagNameToId(tagName: string): string {
  return TAG_NAME_TO_ID[tagName] || tagName;
}

export function tagNamesToIds(tagNames: string[]): string[] {
  return tagNames.map(tagNameToId);
}

export function isValidTagId(tagId: string): boolean {
  return tagId in TAG_ID_TO_NAME;
}
