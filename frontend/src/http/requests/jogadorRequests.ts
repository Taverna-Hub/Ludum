import apiClient from '../axios';

export interface JogadorResumo {
  id: string;
  nome: string;
}

/**
 * Lista todos os jogadores disponíveis no sistema
 */
export const listarJogadores = async (): Promise<JogadorResumo[]> => {
  const response = await apiClient.get<JogadorResumo[]>('/jogadores');
  return response.data;
};
