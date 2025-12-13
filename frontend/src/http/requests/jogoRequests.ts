import { getRequest } from './baseRequests';

// ============================================
// Tipos para Jogos
// ============================================

export interface JogoResponse {
  id: string;
  title: string;
  slug: string;
  description: string;
  price: number;
  originalPrice: number | null;
  coverImage: string;
  screenshots: string[];
  tags: string[];
  developerId: string;
  developerName: string;
  rating: number;
  reviewCount: number;
  releaseDate: string | null;
  earlyAccess: boolean;
  hasAdultContent: boolean;
  modsEnabled: boolean;
  downloadCount: number;
}

// Tipo compatível com o frontend existente (Game interface)
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

// ============================================
// Funções de API
// ============================================

/**
 * Lista todos os jogos publicados
 */
export async function listarJogos(): Promise<Game[]> {
  const response: JogoResponse[] = await getRequest('/jogos');
  return response.map(transformJogoResponse);
}

/**
 * Obtém um jogo específico por ID ou slug
 */
export async function obterJogo(idOuSlug: string): Promise<Game | null> {
  try {
    const response: JogoResponse = await getRequest(`/jogos/${idOuSlug}`);
    return transformJogoResponse(response);
  } catch (error) {
    console.error('Erro ao obter jogo:', error);
    return null;
  }
}

// ============================================
// Helpers
// ============================================

/**
 * Transforma a resposta do backend para o formato usado no frontend
 */
function transformJogoResponse(jogo: JogoResponse): Game {
  return {
    id: jogo.id,
    title: jogo.title,
    slug: jogo.slug,
    description: jogo.description,
    price: jogo.price,
    originalPrice: jogo.originalPrice ?? undefined,
    coverImage: jogo.coverImage || 'https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=800',
    screenshots: jogo.screenshots || [],
    tags: jogo.tags || [],
    developerId: jogo.developerId,
    developerName: jogo.developerName,
    rating: jogo.rating,
    reviewCount: jogo.reviewCount,
    releaseDate: jogo.releaseDate || new Date().toISOString().split('T')[0],
    isEarlyAccess: jogo.earlyAccess,
    hasAdultContent: jogo.hasAdultContent,
    modsEnabled: jogo.modsEnabled,
    downloadCount: jogo.downloadCount,
  };
}
