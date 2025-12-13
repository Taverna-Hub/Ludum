package org.ludum.aplicacao.oficina.mod;

import java.util.List;
import java.util.Optional;

public interface ModRepositorioAplicacao {
    Optional<ModDetalhadoDto> buscarDetalhado(String modId);
    List<ModResumo> listarPorJogo(String jogoId);
    
    // Métodos adicionais para o serviço
    List<ModResumo> pesquisarResumosPorJogo(String jogoId);
    ModResumo buscarResumoPorId(String modId);
}
