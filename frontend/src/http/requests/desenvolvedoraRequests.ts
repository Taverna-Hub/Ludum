import apiClient from '../axios';

export interface DesenvolvedoraResumo {
  id: string;
  nome: string;
}

/**
 * Lista todas as desenvolvedoras disponíveis no sistema
 */
export const listarDesenvolvedoras = async (): Promise<DesenvolvedoraResumo[]> => {
  const response = await apiClient.get<DesenvolvedoraResumo[]>('/desenvolvedoras');
  return response.data;
};
