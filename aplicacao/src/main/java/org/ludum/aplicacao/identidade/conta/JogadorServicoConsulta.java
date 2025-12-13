package org.ludum.aplicacao.identidade.conta;

import java.util.List;

/**
 * Serviço de consulta para jogadores.
 */
public interface JogadorServicoConsulta {
    List<JogadorResumo> listarTodos();
}
