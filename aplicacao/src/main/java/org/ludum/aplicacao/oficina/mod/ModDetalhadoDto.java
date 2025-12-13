package org.ludum.aplicacao.oficina.mod;

import java.time.LocalDateTime;
import java.util.List;

public class ModDetalhadoDto {
    public String id;
    public String jogoId;
    public String autorId;
    public String nome;
    public String descricao;
    public String status;
    public List<VersaoDto> versoes;

    public static class VersaoDto {
        public String notasDeAtualizacao;
        public LocalDateTime dataDeEnvio;
    }
}
