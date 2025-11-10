package org.ludum.dominio.crowdfunding.repositorios;

import java.util.List;
import java.util.Optional;

import org.ludum.dominio.crowdfunding.entidades.Apoio;
import org.ludum.dominio.crowdfunding.entidades.CampanhaId;

public interface ApoioRepository {
    void salvar(Apoio apoio);
    Optional<Apoio> buscarPorId(String id);
    List<Apoio> obterApoiosDaCampanha(CampanhaId id);
}
