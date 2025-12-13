import api from '../axios';
import { Game } from '@/data/mockData';

export interface AdicionarJogoRequest {
    jogoId: string;
    contaId: string;
    modeloDeAcesso: string;
    transacaoId?: string;
}

export const adicionarJogo = async (request: AdicionarJogoRequest): Promise<void> => {
    await api.post('/biblioteca/adicionar', request);
};

export const downloadJogo = async (jogoId: string, contaId: string): Promise<void> => {
    const response = await api.get(`/biblioteca/download/${jogoId}`, {
        params: { contaId },
        responseType: 'blob'
    });

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;

    const contentDisposition = response.headers['content-disposition'];
    const fileNameMatch = contentDisposition ? contentDisposition.match(/filename="(.+)"/) : null;
    const fileName = fileNameMatch ? fileNameMatch[1] : `${jogoId}.zip`;

    link.setAttribute('download', fileName);
    document.body.appendChild(link);
    link.click();
    link.remove();
};

export const verificarPosse = async (jogoId: string, contaId: string): Promise<boolean> => {
    const response = await api.get<boolean>(`/biblioteca/tem-jogo/${jogoId}`, {
        params: { contaId }
    });
    return response.data;
};

export const obterBiblioteca = async (contaId: string): Promise<Game[]> => {
    const response = await api.get<Game[]>('/biblioteca', {
        params: { contaId }
    });
    return response.data;
};
