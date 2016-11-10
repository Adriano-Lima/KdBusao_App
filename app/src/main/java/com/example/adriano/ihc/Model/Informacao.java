package com.example.adriano.ihc.Model;

/**
 * Created by adriano on 07/11/16.
 */

public class Informacao {
    private int id;
    private String cidade, empresa, link;

    public Informacao() {
    }

    public Informacao(int id, String cidade, String empresa, String link) {
        this.id = id;
        this.cidade = cidade;
        this.empresa = empresa;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
