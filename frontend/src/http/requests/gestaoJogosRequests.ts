import api from "../axios";

export const gestaoJogosRequests = {
    uploadJogo: async (
        jogoId: string,
        file: File,
        nomeVersao: string,
        descricao: string,
        contaId: string
    ): Promise<void> => {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("nomeVersao", nomeVersao);
        formData.append("descricao", descricao);
        formData.append("contaId", contaId);

        // O header 'Content-Type': 'multipart/form-data' é setado automaticamente pelo axios/browser quando enviamos FormData
        await api.post(`/jogos/upload/${jogoId}`, formData);
    },
};
