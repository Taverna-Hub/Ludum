package org.ludum.aplicacao.tag;

import java.util.List;

/**
 * Serviço de aplicação para consultas de tags.
 */
public interface TagServicoConsulta {

    /**
     * Lista todas as tags disponíveis no sistema.
     * 
     * @return Lista de resumos de tags (ID e nome)
     */
    List<TagResumo> listarTodas();
}
