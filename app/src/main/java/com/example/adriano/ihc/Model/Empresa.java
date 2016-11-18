package com.example.adriano.ihc.Model;

import java.io.Serializable;

/**
 * Created by adriano on 18/11/16.
 */

public class Empresa implements Serializable{

    private int id;
    private String nome, tipo, descricao, cidade, endereco;
    private String imagem, icon;
    private double latitude, longitude;

    public Empresa() {
    }

    public Empresa(int id, String nome, String tipo, String descricao, String cidade, String endereco, double latitude, double longitude) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.descricao = descricao;
        this.cidade = cidade;
        this.endereco = endereco;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

//    public byte[] getImagem() {
//        return imagem;
//    }
//
//    public void setImagem(byte[] imagem) {
//        this.imagem = imagem;
//    }
//
//    public byte[] getIcon() {
//        return icon;
//    }
//
//    public void setIcon(byte[] icon) {
//        this.icon = icon;
//    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

}
