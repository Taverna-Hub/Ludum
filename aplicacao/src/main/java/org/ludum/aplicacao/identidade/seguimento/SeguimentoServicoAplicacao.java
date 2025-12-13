package org.ludum.aplicacao.identidade.seguimento;

import java.util.List;

/**
 * Interface do serviço de aplicação para operações de consulta de seguimentos.
 */
public interface SeguimentoServicoAplicacao {

    /**
     * Lista quem o usuário está seguindo com informações agregadas.
     */
    List<SeguimentoResumo> listarSeguindo(String seguidorId);

    /**
     * Lista os seguidores de um usuário com informações agregadas.
     */
    List<SeguimentoResumo> listarSeguidores(String alvoId);

    /**
     * Verifica se um usuário está seguindo um alvo.
     */
    boolean estaSeguindo(String seguidorId, String alvoId);

    /**
     * Conta o número de seguidores de um alvo.
     */
    long contarSeguidores(String alvoId);

    /**
     * Conta quantos alvos um usuário está seguindo.
     */
    long contarSeguindo(String seguidorId);
}
