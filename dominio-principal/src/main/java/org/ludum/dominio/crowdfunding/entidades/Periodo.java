package org.ludum.dominio.crowdfunding.entidades;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Periodo {
    private final LocalDateTime dataInicio;
    private final LocalDateTime dataFim;

    public Periodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data de início não pode ser posterior à data do fim.");
        }
        this.dataInicio = Objects.requireNonNull(dataInicio);
        this.dataFim = Objects.requireNonNull(dataFim);
    }

    public boolean isAtivo() {
        LocalDateTime agora = LocalDateTime.now();
        return (agora.isEqual(dataInicio) || agora.isAfter(dataInicio) && agora.isBefore(dataFim));
    }

    public String getTempoRestante() {
        if (!isAtivo()) {
            return "Campanha não está ativa.";
        }
        Duration duracao = Duration.between(LocalDateTime.now(), dataFim);
        long dias = duracao.toDays();
        long horas = duracao.toHours() % 24;
        return String.format("%d dias e %d horas restantes", dias, horas);
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }
}
