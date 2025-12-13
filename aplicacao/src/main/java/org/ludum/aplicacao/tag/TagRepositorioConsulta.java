package org.ludum.aplicacao.tag;

import java.util.List;

/**
 * Repositório de consulta para tags na camada de aplicação.
 * Contém operações de leitura otimizadas para queries.
 */
public interface TagRepositorioConsulta {

    /**
     * Lista todas as tags disponíveis no sistema.
     * 
     * @return Lista de resumos de tags
     */
    List<TagResumo> listarTodas();
}
