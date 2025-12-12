package org.ludum.dominio.catalogo.jogo.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ResultadoPublicacao {
    
    private final boolean sucesso;
    private final List<String> erros;
    private final String mensagem;
    
    private ResultadoPublicacao(boolean sucesso, List<String> erros, String mensagem) {
        this.sucesso = sucesso;
        this.erros = erros != null ? new ArrayList<>(erros) : new ArrayList<>();
        this.mensagem = mensagem;
    }
    
    public static ResultadoPublicacao sucesso() {
        return new ResultadoPublicacao(true, Collections.emptyList(), "Jogo publicado com sucesso");
    }
    
    public static ResultadoPublicacao sucesso(String mensagem) {
        return new ResultadoPublicacao(true, Collections.emptyList(), mensagem);
    }
    
    public static ResultadoPublicacao comErros(List<String> erros) {
        String mensagem = erros.isEmpty() 
            ? "Erro desconhecido" 
            : String.format("Foram encontrados %d erro(s) de validação", erros.size());
        return new ResultadoPublicacao(false, erros, mensagem);
    }
    
    public static ResultadoPublicacao erro(String mensagemErro) {
        List<String> erros = new ArrayList<>();
        erros.add(mensagemErro);
        return new ResultadoPublicacao(false, erros, mensagemErro);
    }
    
    public boolean isSucesso() {
        return sucesso;
    }
    
    public boolean isFalha() {
        return !sucesso;
    }
    
    public List<String> getErros() {
        return Collections.unmodifiableList(erros);
    }
    
    public String getMensagem() {
        return mensagem;
    }
    
    public boolean temErros() {
        return !erros.isEmpty();
    }
    
    @Override
    public String toString() {
        if (sucesso) {
            return "ResultadoPublicacao{sucesso=true, mensagem='" + mensagem + "'}";
        }
        return "ResultadoPublicacao{sucesso=false, erros=" + erros.size() + ", mensagem='" + mensagem + "'}";
    }
}
