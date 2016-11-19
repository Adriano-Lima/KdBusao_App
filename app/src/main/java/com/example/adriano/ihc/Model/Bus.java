package com.example.adriano.ihc.Model;

import java.util.ArrayList;

public class Bus {

    private int id;
    private String linha, sentido;
    private double lat, longi;

    public int getId() {
        return id;
    }

    public String getLinha() {
        return linha;
    }

    public double getLat() {
        return lat;
    }

    public double getLongi() {
        return longi;
    }

    public Bus(int id, String linha, double lat, double longi, String sentido) {
        super();
        this.id = id;
        this.linha = linha;
        this.lat = lat;
        this.longi = longi;
        this.sentido = sentido;
    }

    public Bus(String id, String lat, String longi) {
        super();
        this.id = Integer.parseInt(id);
        this.lat = Double.parseDouble(lat);
        this.longi = Double.parseDouble(longi);
        this.linha = "";
        this.sentido = "";
    }

    public Bus(String id, String lat, String longi, String sentido) {
        super();
        this.id = Integer.parseInt(id);
        this.lat = Double.parseDouble(lat);
        this.longi = Double.parseDouble(longi);
        this.linha = "";
        this.sentido = sentido;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
