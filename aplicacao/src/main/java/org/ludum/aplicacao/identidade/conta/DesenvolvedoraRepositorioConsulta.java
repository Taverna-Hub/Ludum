package org.ludum.aplicacao.identidade.conta;

import java.util.List;

/**
 * Repositório de consulta para contas desenvolvedoras na camada de aplicação.
 */
public interface DesenvolvedoraRepositorioConsulta {

    /**
     * Lista todas as desenvolvedoras ativas no sistema.
     * 
     * @return Lista de resumos de desenvolvedoras
     */
    List<DesenvolvedoraResumo> listarTodas();
}
