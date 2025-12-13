import api from "../axios";

export interface CriarJogoRequest {
  desenvolvedoraId: string;
  titulo: string;
  descricao: string;
  capaOficial: string;
  screenshots: string[];
  videos: string[];
  tags: string[];
  isNSFW: boolean;
  dataDeLancamento: string;
}

export interface PublicarJogoResponse {
  sucesso: boolean;
  jogoId: string;
  mensagem: string;
  status: string;
}

export interface ValidarJogoResponse {
  sucesso: boolean;
  mensagem: string;
  status: string;
}

export interface RejeitarJogoResponse {
  sucesso: boolean;
  mensagem: string;
  status: string;
}

export const jogoRequests = {
  publicarJogo: async (data: CriarJogoRequest): Promise<PublicarJogoResponse> => {
    const response = await api.post("/publicacoes/publicar", data);
    return response.data;
  },

  validarJogo: async (jogoId: string): Promise<ValidarJogoResponse> => {
    const response = await api.post(`/publicacoes/${jogoId}/validar`);
    return response.data;
  },

  rejeitarJogo: async (jogoId: string, motivo: string): Promise<RejeitarJogoResponse> => {
    const response = await api.post(`/publicacoes/${jogoId}/rejeitar`, { motivo });
    return response.data;
  },

  arquivarJogo: async (jogoId: string, desenvolvedoraId: string): Promise<void> => {
    await api.post(`/publicacoes/${jogoId}/arquivar`, { desenvolvedoraId });
  },

  //TODO: REMOVER PARA USAR CONSTANTS 
  buscarTags: async (): Promise<string[]> => {
    return [
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
    ];
  },
};