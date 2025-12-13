import apiClient from '../axios';

export interface TagResumo {
  id: string;
  nome: string;
}

/**
 * Lista todas as tags disponíveis no sistema
 */
export const listarTags = async (): Promise<TagResumo[]> => {
  const response = await apiClient.get<TagResumo[]>('/tags');
  return response.data;
};
