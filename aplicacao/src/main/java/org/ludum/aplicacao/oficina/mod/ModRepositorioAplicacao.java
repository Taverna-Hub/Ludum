package org.ludum.aplicacao.oficina.mod;

import java.util.List;

public interface ModRepositorioAplicacao {
    List<ModResumo> pesquisarResumosPorJogo(String jogoId);
    List<ModResumo> pesquisarResumosPorAutor(String autorId);
    ModResumo buscarResumoPorId(String id);
}
