package org.ludum.aplicacao.identidade.seguimento;

import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.seguimento.entities.AlvoId;
import org.ludum.dominio.identidade.seguimento.entities.Seguimento;

import java.util.List;

/**
 * Repositório de consulta para seguimentos na camada de aplicação.
 * Contém operações de leitura específicas para casos de uso da aplicação.
 */
public interface SeguimentoRepositorioConsulta {

    /**
     * Verifica se um usuário está seguindo um alvo.
     */
    boolean estaSeguindo(ContaId seguidorId, AlvoId alvoId);

    /**
     * Conta o número de seguidores de um alvo.
     */
    long contarSeguidores(AlvoId alvoId);

    /**
     * Conta quantos alvos um usuário está seguindo.
     */
    long contarSeguindo(ContaId seguidorId);

    /**
     * Lista todos os seguimentos de um usuário (quem ele segue).
     */
    List<Seguimento> listarSeguindo(ContaId seguidorId);

    /**
     * Lista todos os seguidores de um alvo.
     */
    List<Seguimento> listarSeguidores(AlvoId alvoId);
}
