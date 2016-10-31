package com.example.adriano.ihc.Model;

import java.util.ArrayList;

public class Bus {
    private final int id;
    private final String linha;
    private final double lat, longi;

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

    public Bus(int id, String linha, double lat, double longi) {
        super();
        this.id = id;
        this.linha = linha;
        this.lat = lat;
        this.longi = longi;
    }

    public Bus(String id, String lat, String longi) {
        super();
        this.id = Integer.parseInt(id);
        this.lat = Double.parseDouble(lat);
        this.longi = Double.parseDouble(longi);
        this.linha="";
    }


}
