package org.ludum.aplicacao.tag.impl;

import org.ludum.aplicacao.tag.TagRepositorioConsulta;
import org.ludum.aplicacao.tag.TagResumo;
import org.ludum.aplicacao.tag.TagServicoConsulta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServicoConsultaImpl implements TagServicoConsulta {

    private final TagRepositorioConsulta tagRepositorioConsulta;

    public TagServicoConsultaImpl(TagRepositorioConsulta tagRepositorioConsulta) {
        this.tagRepositorioConsulta = tagRepositorioConsulta;
    }

    @Override
    public List<TagResumo> listarTodas() {
        return tagRepositorioConsulta.listarTodas();
    }
}
