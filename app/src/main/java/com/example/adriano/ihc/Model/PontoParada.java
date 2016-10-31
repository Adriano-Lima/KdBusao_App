package com.example.adriano.ihc.Model;

public class PontoParada {
    private String coordenadas, endereco, descricao;

    public PontoParada() {
    }

    public PontoParada(String coordenadas, String endereco, String descricao) {
        this.coordenadas = coordenadas;
        this.endereco = endereco;
        this.descricao = descricao;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}