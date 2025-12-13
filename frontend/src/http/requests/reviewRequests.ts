import { getRequest, postRequest, putRequest, deleteRequest } from './baseRequests';

// ============================================
// Tipos para Reviews
// ============================================

export interface ReviewResponse {
  id: string;
  jogoId: string;
  autorId: string;
  autorNome: string;
  nota: number;
  titulo: string;
  texto: string;
  data: string;
  dataUltimaEdicao: string | null;
  recomendado: boolean;
  status: 'PUBLICADA' | 'EDITADO' | 'EXCLUIDO';
}

export interface ReviewResumo {
  mediaEstrelas: number;
  totalRecomendacoes: number;
  porcentagemRecomendacoes: number;
}

export interface CriarReviewData {
  nota: number;
  titulo: string;
  texto: string;
  recomenda: boolean;
}

export interface EditarReviewData {
  nota: number;
  titulo: string;
  texto: string;
  recomenda: boolean;
}

// ============================================
// Funções de API
// ============================================

/**
 * Cria uma nova review para um jogo
 */
export async function criarReview(jogoId: string, data: CriarReviewData): Promise<void> {
  return postRequest(`/jogos/${jogoId}/reviews`, data);
}

/**
 * Edita uma review existente
 */
export async function editarReview(reviewId: string, data: EditarReviewData): Promise<void> {
  return putRequest(`/reviews/${reviewId}`, data);
}

/**
 * Remove uma review
 */
export async function removerReview(reviewId: string): Promise<void> {
  return deleteRequest(`/reviews/${reviewId}`);
}

/**
 * Lista reviews de um jogo
 * @param jogoId - ID do jogo
 * @param nota - Filtrar por nota (opcional)
 * @param ordenarPorData - Se deve ordenar por data (opcional)
 * @param maisRecentes - Se as mais recentes vêm primeiro (padrão: true)
 */
export async function listarReviews(
  jogoId: string,
  params?: {
    nota?: number;
    ordenarPorData?: boolean;
    maisRecentes?: boolean;
  }
): Promise<ReviewResponse[]> {
  return getRequest(`/jogos/${jogoId}/reviews`, params);
}

/**
 * Obtém o resumo das reviews de um jogo (média, total de recomendações, etc)
 */
export async function obterResumoReviews(jogoId: string): Promise<ReviewResumo> {
  return getRequest(`/jogos/${jogoId}/reviews/resumo`);
}

// ============================================
// Helpers para transformar dados
// ============================================

export interface ReviewFrontend {
  id: string;
  gameId: string;
  userId: string;
  userName: string;
  rating: number;
  title: string;
  comment: string;
  recommended: boolean;
  date: string;
  updatedAt?: string;
  helpful: number;
  deleted?: boolean;
}

/**
 * Transforma a resposta do backend para o formato usado no frontend
 */
export function transformReviewResponse(review: ReviewResponse): ReviewFrontend {
  return {
    id: review.id,
    gameId: review.jogoId,
    userId: review.autorId,
    userName: review.autorNome || 'Usuário',
    rating: review.nota,
    title: review.titulo,
    comment: review.texto,
    recommended: review.recomendado,
    date: review.data,
    updatedAt: review.dataUltimaEdicao || undefined,
    helpful: 0, // Backend não implementa isso ainda
    deleted: review.status === 'EXCLUIDO',
  };
}
