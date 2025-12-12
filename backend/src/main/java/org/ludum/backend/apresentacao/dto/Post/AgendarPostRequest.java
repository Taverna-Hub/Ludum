package org.ludum.backend.apresentacao.dto.Post;

import java.time.LocalDateTime;

public class AgendarPostRequest {
    
    private LocalDateTime dataAgendamento;
    
    public AgendarPostRequest() {
    }
    
    public AgendarPostRequest(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }
    
    // Getters e Setters
    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }
    
    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }
}
