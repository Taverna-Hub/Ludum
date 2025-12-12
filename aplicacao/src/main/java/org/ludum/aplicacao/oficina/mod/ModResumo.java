package org.ludum.aplicacao.oficina.mod;

import org.ludum.dominio.oficina.mod.enums.StatusMod;

public interface ModResumo {
    String getId();
    String getNome();
    String getDescricao();
    StatusMod getStatus();
    String getNomeAutor();
}
