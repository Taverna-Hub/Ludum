package org.ludum.crowdfunding.repositorios;

import java.util.List;

import org.ludum.crowdfunding.entidades.Apoio;
import org.ludum.crowdfunding.entidades.CampanhaId;

public interface ApoioRepository {
    void salvar(Apoio apoio);
    List<Apoio> obterApoiosDaCampanha(CampanhaId id);
}
