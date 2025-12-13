package org.ludum.aplicacao.identidade.conta.impl;

import org.ludum.aplicacao.identidade.conta.DesenvolvedoraRepositorioConsulta;
import org.ludum.aplicacao.identidade.conta.DesenvolvedoraResumo;
import org.ludum.aplicacao.identidade.conta.DesenvolvedoraServicoConsulta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesenvolvedoraServicoConsultaImpl implements DesenvolvedoraServicoConsulta {

    private final DesenvolvedoraRepositorioConsulta desenvolvedoraRepositorioConsulta;

    public DesenvolvedoraServicoConsultaImpl(DesenvolvedoraRepositorioConsulta desenvolvedoraRepositorioConsulta) {
        this.desenvolvedoraRepositorioConsulta = desenvolvedoraRepositorioConsulta;
    }

    @Override
    public List<DesenvolvedoraResumo> listarTodas() {
        return desenvolvedoraRepositorioConsulta.listarTodas();
    }
}
