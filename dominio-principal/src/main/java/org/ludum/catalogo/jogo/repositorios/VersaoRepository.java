package org.ludum.catalogo.jogo.repositorios;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.entidades.Versao;
import org.ludum.catalogo.jogo.entidades.VersaoId;

import java.util.List;

public interface VersaoRepository {

    Versao obterPorId(VersaoId versaoId);

    List<Versao> obterPorJogoId(JogoId jogoId);

}