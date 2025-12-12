import { getRequest, postRequest } from './baseRequests';

export interface Transacao {
  id: string;
  tipo: string;
  status: string;
  data: string;
  valor: number;
  contaOrigemId: string | null;
  contaDestinoId: string | null;
  descricao: string;
}

export interface Carteira {
  id: string;
  disponivel: number;
  bloqueado: number;
  contaExternaValida: boolean;
}

export interface ComprarJogoRequest {
  jogoId: string;
  compradorId: string;
  desenvolvedoraId: string;
  valor: number;
}

export interface ComprarJogoResponse {
  sucesso: boolean;
  mensagem: string;
  transacaoId: string | null;
  saldoAtual?: number;
  valorFaltante?: number;
}

export async function getTransacoesPorConta(
  contaId: string,
): Promise<Transacao[]> {
  return getRequest(`/carteira/${contaId}/transacoes`);
}

export async function getCarteiraPorConta(contaId: string): Promise<Carteira> {
  return getRequest(`/carteira/${contaId}`);
}

export async function comprarJogo(
  request: ComprarJogoRequest,
): Promise<ComprarJogoResponse> {
  return postRequest('/jogos/comprar', request);
}
