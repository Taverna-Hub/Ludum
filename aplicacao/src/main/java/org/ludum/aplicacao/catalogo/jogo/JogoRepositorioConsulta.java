package org.ludum.aplicacao.catalogo.jogo;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.util.List;

/**
 * Repositório de consulta para jogos na camada de aplicação.
 * Contém operações de leitura específicas para casos de uso da aplicação.
 * Separado do repositório de domínio para manter responsabilidades distintas.
 */
public interface JogoRepositorioConsulta {

    /**
     * Lista todos os jogos com status PUBLICADO.
     * 
     * @return Lista de jogos publicados
     */
    List<Jogo> listarTodosPublicados();

    /**
     * Lista todos os jogos de uma desenvolvedora específica.
     * 
     * @param devId ID da desenvolvedora
     * @return Lista de jogos da desenvolvedora
     */
    List<Jogo> listarPorDesenvolvedora(ContaId devId);
}
