package org.ludum.aplicacao.oficina.mod.servico;

public interface ModServicoAplicacao {
    /* Publica um mod já com sua primeira versão.
     *
     * @return id do mod criado
     */
    String publicarMod(
            String jogoId,
            String autorId,
            String nome,
            String descricao,
            String notasVersao,
            byte[] arquivo
    );

    /*
     * Envia nova versão para um mod existente.
     */
    void enviarNovaVersao(
            String modId,
            String autorId,
            String notasVersao,
            byte[] arquivo
    );

    /*
     * Atualiza metadados do mod.
     */
    void atualizarMod(
            String modId,
            String autorId,
            String novoNome,
            String novaDescricao
    );

    /*
     * Remove/desativa o mod (conforme regra do domínio).
     */
    void removerMod(String modId, String autorId);
}
