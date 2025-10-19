package org.ludum.catalogo.jogo.entidades;

public class Versao {
    private final VersaoId id;
    private final JogoId jogoId;
    private final PacoteZip pacoteZip;
    //Titulo_Versao.Versao.Versao, onde Versao é composto apenas por números inteiros(Ex: JogoDaCobrinha_2.5.9)
    private String nomeVersao;
    private String descricaoVersao;


    public Versao(PacoteZip pacoteZip, JogoId jogoId, VersaoId versaoId, String nomeVersao, String descricaoVersao) {
        validarPacoteZip(pacoteZip);
        this.pacoteZip = pacoteZip;
        this.jogoId = jogoId;
        this.id = versaoId;
        validarNomeVersao(nomeVersao);
        this.nomeVersao = nomeVersao;
        this.descricaoVersao = descricaoVersao;
    }

    private void validarPacoteZip(PacoteZip pacoteZip) {
        //TODO: Checagem de estrutura
        if (pacoteZip.getConteudo().length == 0) {
            throw new IllegalStateException("Necessário o upload do pacote");
        }
    }

    private void validarNomeVersao(String nomeVersao) {
        if (nomeVersao.isEmpty()) {
            throw new IllegalArgumentException("Nome da versão vazio");
        }
        String[] list = nomeVersao.split("_");
        String[] digitos = list[1].split("\\.");
        if(list.length != 2 || digitos.length != 3) {
            throw new IllegalArgumentException("Nome da versão não está formatado corretamente");
        }
        for(int i = 0; i < digitos.length; i++) {
            try{
                Integer.parseInt(digitos[i]);
            }catch(Exception e){
                throw new IllegalArgumentException("Versão só pode conter números inteiros");
            }
        }
    }

    public void atualizarDescricao(String descricaoNew) {
        if(descricaoNew == null || descricaoNew.isEmpty()) {
            throw new IllegalArgumentException("Descrição inválida");
        }
        this.descricaoVersao = descricaoNew;
    }

    public void atualizaNomeVersao(String nomeNew) {
        validarNomeVersao(nomeNew);
        this.nomeVersao = nomeNew;
    }

    public String getDescricaoVersao() {
        return descricaoVersao;
    }

    public String getNomeVersao() {
        return nomeVersao;
    }

    public PacoteZip getPacoteZip() {
        return pacoteZip;
    }

    public JogoId getJogoId() {
        return jogoId;
    }

    public VersaoId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Versao versao = (Versao) o;
        return id.equals(versao.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}