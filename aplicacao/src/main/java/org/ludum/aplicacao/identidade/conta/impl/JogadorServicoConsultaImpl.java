package org.ludum.aplicacao.identidade.conta.impl;

import org.ludum.aplicacao.identidade.conta.JogadorRepositorioConsulta;
import org.ludum.aplicacao.identidade.conta.JogadorResumo;
import org.ludum.aplicacao.identidade.conta.JogadorServicoConsulta;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementação do serviço de consulta de jogadores.
 */
@Service
public class JogadorServicoConsultaImpl implements JogadorServicoConsulta {

    private final JogadorRepositorioConsulta jogadorRepositorioConsulta;

    public JogadorServicoConsultaImpl(JogadorRepositorioConsulta jogadorRepositorioConsulta) {
        this.jogadorRepositorioConsulta = jogadorRepositorioConsulta;
    }

    @Override
    public List<JogadorResumo> listarTodos() {
        return jogadorRepositorioConsulta.listarTodos();
    }
}
