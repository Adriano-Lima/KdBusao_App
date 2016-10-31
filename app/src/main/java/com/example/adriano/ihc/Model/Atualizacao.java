package com.example.adriano.ihc.Model;

import java.io.Serializable;

public class Atualizacao implements Serializable {
    private int contador;
    private String idOnibus, localizacao, data, distancia, velocidade;

    public Atualizacao(String id, int contador, String localizacao, String data, String distancia, String velocidade) {
        this.idOnibus = id;
        this.contador = contador;
        this.localizacao = localizacao;
        this.data = data;
        this.distancia = distancia;
        this.velocidade = velocidade;
    }

    public Atualizacao(String id, int contador, String localizacao, String data) {
        this.idOnibus = id;
        this.contador = contador;
        this.localizacao = localizacao;
        this.data = data;
    }

    public Atualizacao() {
    }

    public int getContador() {
        return contador;
    }

    public void setContador(int contador) {
        this.contador = contador;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getId() {
        return idOnibus;
    }

    public void setId(String id) {
        this.idOnibus = id;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getIdOnibus() {
        return idOnibus;
    }

    public void setIdOnibus(String idOnibus) {
        this.idOnibus = idOnibus;
    }

    public String getVelocidade() {
        return velocidade;
    }

    public void setVelocidade(String velocidade) {
        this.velocidade = velocidade;
    }
}
