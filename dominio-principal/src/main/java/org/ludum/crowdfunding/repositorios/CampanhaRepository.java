package org.ludum.crowdfunding.repositorios;

import java.util.List;
import java.util.Optional;

import org.ludum.crowdfunding.entidades.Campanha;
import org.ludum.crowdfunding.entidades.CampanhaId;

public interface CampanhaRepository {
    void salvar(Campanha campanha);
    void remover(Campanha campanha);
    Optional<Campanha> buscarPorId(CampanhaId id);
    List<Campanha> listarCampanhasAtivas();
}
