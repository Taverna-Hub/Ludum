package org.ludum.aplicacao.identidade.conta;

import java.util.List;

/**
 * Porta de consulta para jogadores na camada de aplicação.
 */
public interface JogadorRepositorioConsulta {
    List<JogadorResumo> listarTodos();
}
