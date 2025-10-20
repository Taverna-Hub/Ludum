package org.ludum.crowdfunding.repositorios;

import java.util.List;
import java.util.Optional;

import org.ludum.crowdfunding.entidades.Apoio;
import org.ludum.crowdfunding.entidades.CampanhaId;

public interface ApoioRepository {
    void salvar(Apoio apoio);
    Optional<Apoio> buscarPorId(String id);
    List<Apoio> obterApoiosDaCampanha(CampanhaId id);
}
