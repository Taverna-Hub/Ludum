import api from '../axios';

// Interfaces
export interface PostResponse {
  id: string;
  jogoId: string;
  autorId: string;
  titulo: string;
  conteudo: string;
  dataPublicacao: string;
  dataAgendamento?: string;
  imagemUrl?: string;
  status: 'EM_RASCUNHO' | 'AGENDADO' | 'PUBLICADO';
  tagIds: string[];
  numeroCurtidas: number;
  numeroComentarios: number;
}

export interface CriarPostRequest {
  jogoId: string;
  autorId: string;
  titulo: string;
  conteudo: string;
  imagemUrl?: string;
  tagIds: string[];
}

export interface EditarPostRequest {
  titulo: string;
  conteudo: string;
}

export interface ComentarPostRequest {
  autorId: string;
  texto: string;
}

export interface AgendarPostRequest {
  dataAgendamento: string;
}

// Funções de requisição
export const postRequests = {
  // Obter todos os posts
  obterTodosOsPosts: async (): Promise<PostResponse[]> => {
    try {
      const response = await api.get('/posts');
      return response.data;
    } catch (error) {
      console.error('Erro ao obter posts:', error);
      throw error;
    }
  },

  // Publicar post
  publicarPost: async (data: CriarPostRequest): Promise<PostResponse> => {
    try {
      const response = await api.post('/posts/publicar', data);
      return response.data;
    } catch (error) {
      console.error('Erro ao publicar post:', error);
      throw error;
    }
  },

  // Criar rascunho
  criarRascunho: async (data: CriarPostRequest): Promise<PostResponse> => {
    try {
      const response = await api.post('/posts/rascunho', data);
      return response.data;
    } catch (error) {
      console.error('Erro ao criar rascunho:', error);
      throw error;
    }
  },

  // Obter post por ID
  obterPostPorId: async (id: string): Promise<PostResponse> => {
    try {
      const response = await api.get(`/posts/${id}`);
      return response.data;
    } catch (error) {
      console.error('Erro ao obter post:', error);
      throw error;
    }
  },

  // Editar post
  editarPost: async (
    id: string,
    contaId: string,
    data: EditarPostRequest
  ): Promise<PostResponse> => {
    try {
      const response = await api.put(`/posts/${id}`, data, {
        headers: {
          'X-Conta-Id': contaId,
        },
      });
      return response.data;
    } catch (error) {
      console.error('Erro ao editar post:', error);
      throw error;
    }
  },

  // Remover post
  removerPost: async (id: string, contaId: string): Promise<void> => {
    try {
      await api.delete(`/posts/${id}`, {
        headers: {
          'X-Conta-Id': contaId,
        },
      });
    } catch (error) {
      console.error('Erro ao remover post:', error);
      throw error;
    }
  },

  // Curtir post
  curtirPost: async (id: string, contaId: string): Promise<void> => {
    try {
      await api.post(`/posts/${id}/curtir`, null, {
        headers: {
          'X-Conta-Id': contaId,
        },
      });
    } catch (error) {
      console.error('Erro ao curtir post:', error);
      throw error;
    }
  },

  // Descurtir post
  descurtirPost: async (id: string, contaId: string): Promise<void> => {
    try {
      await api.delete(`/posts/${id}/curtir`, {
        headers: {
          'X-Conta-Id': contaId,
        },
      });
    } catch (error) {
      console.error('Erro ao descurtir post:', error);
      throw error;
    }
  },

  // Comentar post
  comentarPost: async (id: string, data: ComentarPostRequest): Promise<void> => {
    try {
      await api.post(`/posts/${id}/comentarios`, data);
    } catch (error) {
      console.error('Erro ao comentar post:', error);
      throw error;
    }
  },

  // Remover comentário
  removerComentario: async (
    postId: string,
    comentarioId: string,
    contaId: string
  ): Promise<void> => {
    try {
      await api.delete(`/posts/${postId}/comentarios/${comentarioId}`, {
        headers: {
          'X-Conta-Id': contaId,
        },
      });
    } catch (error) {
      console.error('Erro ao remover comentário:', error);
      throw error;
    }
  },

  // Agendar post
  agendarPost: async (
    id: string,
    data: AgendarPostRequest
  ): Promise<PostResponse> => {
    try {
      const response = await api.post(`/posts/${id}/agendar`, data);
      return response.data;
    } catch (error) {
      console.error('Erro ao agendar post:', error);
      throw error;
    }
  },

  // Publicar rascunho
  publicarRascunho: async (id: string, contaId: string): Promise<PostResponse> => {
    try {
      const response = await api.post(`/posts/${id}/publicar`, null, {
        headers: {
          'X-Conta-Id': contaId,
        },
      });
      return response.data;
    } catch (error) {
      console.error('Erro ao publicar rascunho:', error);
      throw error;
    }
  },

  // Obter posts por autor
  obterPostsPorAutor: async (contaId: string): Promise<PostResponse[]> => {
    try {
      const response = await api.get(`/posts/autor/${contaId}`);
      return response.data;
    } catch (error) {
      console.error('Erro ao obter posts por autor:', error);
      throw error;
    }
  },

  // Buscar posts por tag
  buscarPostsPorTag: async (tag: string): Promise<PostResponse[]> => {
    try {
      const response = await api.get(`/posts/tag/${tag}`);
      return response.data;
    } catch (error) {
      console.error('Erro ao buscar posts por tag:', error);
      throw error;
    }
  },

  // Obter posts por status
  obterPostsPorStatus: async (
    status: 'RASCUNHO' | 'AGENDADO' | 'PUBLICADO'
  ): Promise<PostResponse[]> => {
    try {
      const response = await api.get(`/posts/status/${status}`);
      return response.data;
    } catch (error) {
      console.error('Erro ao obter posts por status:', error);
      throw error;
    }
  },
};
