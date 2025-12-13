package org.ludum.aplicacao.identidade.conta;

import java.util.List;

/**
 * Serviço de aplicação para consultas de desenvolvedoras.
 */
public interface DesenvolvedoraServicoConsulta {

    /**
     * Lista todas as desenvolvedoras ativas no sistema.
     * 
     * @return Lista de resumos de desenvolvedoras (ID e nome)
     */
    List<DesenvolvedoraResumo> listarTodas();
}
