import { getRequest } from './baseRequests';

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

export async function getTransacoesPorConta(
  contaId: string,
): Promise<Transacao[]> {
  return getRequest(`/carteira/${contaId}/transacoes`);
}

export async function getCarteiraPorConta(contaId: string): Promise<Carteira> {
  return getRequest(`/carteira/${contaId}`);
}
