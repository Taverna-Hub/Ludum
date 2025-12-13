package org.ludum.aplicacao.oficina.mod;

import java.time.LocalDateTime;

public interface ModResumo {
    String getId();
    String getNome();
    String getDescricao();
    String getAutorId();
    String getStatus();
    int getTotalVersoes();
    LocalDateTime getDataUltimoEnvio();
}
