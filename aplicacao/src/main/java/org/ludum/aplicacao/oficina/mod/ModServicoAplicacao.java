package org.ludum.aplicacao.oficina.mod;

import java.util.List;

public interface ModServicoAplicacao {
    // Escrita
    void publicarNovoMod(String jogoId, String autorId, String nome, String descricao, String notas, byte[] arquivo);

    void lancarNovaVersao(String modId, String autorId, String notas, byte[] arquivo);

    void atualizarDetalhes(String modId, String autorId, String novoNome, String novaDescricao);

    void removerMod(String modId, String autorId);

    // Leitura
    List<ModResumo> pesquisarResumosPorJogo(String jogoId);

    ModResumo buscarPorId(String modId);
}
