package org.ludum.aplicacao.oficina.mod.query;

import java.util.List;
import java.util.Optional;

public interface ModRepositorioAplicacao {
    Optional<ModDetalhadoDto> buscarDetalhado(String modId);
    List<ModResumo> listarPorJogo(String jogoId);
}
