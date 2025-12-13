import { getRequest, postRequest, deleteRequest } from './baseRequests';

// ============================================
// Tipos para Seguimentos
// ============================================

export type TipoAlvo = 'CONTA' | 'JOGO' | 'DESENVOLVEDORA' | 'TAG';

export interface SeguimentoResponse {
  id: string;
  seguidorId: string;
  alvoId: string;
  tipoAlvo: TipoAlvo;
  dataSeguimento: string;
  alvoNome: string;
}

export interface SeguirRequest {
  alvoId: string;
  tipoAlvo: TipoAlvo;
}

// ============================================
// Funções de API
// ============================================

/**
 * Seguir uma conta, jogo, desenvolvedora ou tag
 */
export async function seguir(data: SeguirRequest): Promise<void> {
  await postRequest('/seguimentos', data);
}

/**
 * Deixar de seguir
 */
export async function deixarDeSeguir(alvoId: string): Promise<void> {
  await deleteRequest(`/seguimentos/${alvoId}`);
}

/**
 * Listar quem o usuário está seguindo
 */
export async function listarSeguindo(): Promise<SeguimentoResponse[]> {
  return await getRequest('/seguimentos/seguindo');
}

/**
 * Listar seguidores do usuário
 */
export async function listarSeguidores(): Promise<SeguimentoResponse[]> {
  return await getRequest('/seguimentos/seguidores');
}

/**
 * Verificar se está seguindo um alvo
 */
export async function estaSeguindo(alvoId: string): Promise<boolean> {
  return await getRequest(`/seguimentos/verificar/${alvoId}`);
}

/**
 * Contar seguidores de um alvo
 */
export async function contarSeguidores(alvoId: string): Promise<number> {
  return await getRequest(`/seguimentos/contar/${alvoId}`);
}

/**
 * Contar quantos alvos um usuário está seguindo
 */
export async function contarSeguindo(seguidorId: string): Promise<number> {
  return await getRequest(`/seguimentos/contar-seguindo/${seguidorId}`);
}
