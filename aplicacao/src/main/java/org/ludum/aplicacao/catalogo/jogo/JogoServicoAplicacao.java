package org.ludum.aplicacao.catalogo.jogo;

import java.util.List;
import java.util.Optional;

/**
 * Interface do serviço de aplicação para operações de consulta de jogos.
 * Responsável por orquestrar a lógica de negócio e agregação de dados.
 */
public interface JogoServicoAplicacao {

    /**
     * Lista todos os jogos publicados com informações agregadas.
     * 
     * @return Lista de resumos de jogos publicados
     */
    List<JogoResumo> listarJogosPublicados();

    /**
     * Obtém um jogo por ID ou slug.
     * 
     * @param idOuSlug ID ou slug do jogo
     * @return Optional com o resumo do jogo ou vazio se não encontrado
     */
    Optional<JogoResumo> obterPorIdOuSlug(String idOuSlug);

    /**
     * Lista jogos de uma desenvolvedora específica.
     * 
     * @param desenvolvedoraId ID da desenvolvedora
     * @return Lista de resumos de jogos da desenvolvedora
     */
    List<JogoResumo> listarPorDesenvolvedora(String desenvolvedoraId);
}
