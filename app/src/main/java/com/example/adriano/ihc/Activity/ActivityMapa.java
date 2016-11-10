package com.example.adriano.ihc.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.adriano.ihc.Controller.ComunicaServidor;
import com.example.adriano.ihc.Model.Bus;
import com.example.adriano.ihc.Model.PontoParada;
import com.example.adriano.ihc.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;

public class ActivityMapa extends FragmentActivity implements OnMapReadyCallback {

    private LinearLayout layout;
    private GoogleMap mMap;
    private HashMap<Integer,Marker> markerHash;
    private PolylineOptions rectOptions;
    private Polyline polyline;
    private Handler handler;
    private int UPDATE_INTERVAL;
    private String nmLinha;
    private AlertDialog alerta = null;
    private AlertDialog.Builder builder;
    private ComunicaServidor comunicaServidor;
    private List<PontoParada> pontosParada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        layout = (LinearLayout)findViewById(R.id.layoutMapa);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        UPDATE_INTERVAL = sharedPreferences.getInt("TempoAtualizacao", 10);
        UPDATE_INTERVAL *= 1000; //convertendo para milisegundos

        handler = new Handler();
        handler.postDelayed(location, UPDATE_INTERVAL);

        markerHash = null;

        if(getIntent().getExtras().containsKey("Linha"))
            nmLinha = getIntent().getExtras().getString("Linha", "");

        comunicaServidor = new ComunicaServidor(ActivityMapa.this);

        mostrarSnackBar("Vamos mostar a localização da linha: " + nmLinha);
    }

    public void mostrarSnackBar(String msg){
        Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void apresentar() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); //apresentar a localização do usuário
        }
        buscarPontosParada();
    }

    private void buscarPontosParada() {
        comunicaServidor.getPontos(nmLinha);
    }

    //função responsável por adicionar os pontos de parada no mapa
    public void exibirPontosParadaMapa(List<PontoParada> pontos) {
        PolylineOptions rectOptions = new PolylineOptions();
        Polyline polyline = mMap.addPolyline(rectOptions);
        polyline.setColor(Color.BLUE);
        List<LatLng> points = rectOptions.getPoints();
        this.pontosParada = pontos;
        for (PontoParada p : pontos) {
            try {
                String coordenadas = p.getCoordenadas();
                String vec[] = coordenadas.split(",");
                double lat = Double.parseDouble(vec[0]);
                double lon = Double.parseDouble(vec[1]);
                LatLng latLng = new LatLng(lat, lon);
                points.add(latLng);
                mMap.addMarker(new MarkerOptions().position(latLng).title(p.getDescricao()));
            } catch (Exception e) {
                Log.e("Teste", "Erro em exibirPontosParadaMapa(List<PontoParada> pontos) >>>:" + e.getMessage());
            }
        }
        polyline.setPoints(points);
    }

    public void setLocalizacaoDosOnibus(List<Bus> lista) {
        if(markerHash == null){
            markerHash = new HashMap<>(lista.size());
        }
        for (Bus bus : lista) {
            updateMarker(bus.getId(),bus.getLat(), bus.getLongi());
        }
    }

    //responsável por fazer as solicitações ao servidor sobre a localização do ônibus
    Runnable location = new Runnable() {
        @Override
        public void run() {
            comunicaServidor.getLocalizacaoLinha(nmLinha);
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    //responsável por atualizar a construção da linha de percurso do ônibus
    private void updatePolyLine(LatLng latLng) {
        List<LatLng> points = rectOptions.getPoints();
        points.add(latLng);
        polyline.setPoints(points);
    }

    //responsável por atualizar a posição do Marker no mapa(localização do ônibus)
    private void updateMarker(int idOnibus, double x, double y) {
        LatLng latLng = new LatLng(x, y);
        if(!markerHash.containsKey(idOnibus)){
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(nmLinha).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
            marker.showInfoWindow(); //para deixar o título sempre visível
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            markerHash.put(idOnibus,marker);
        }else{
            markerHash.get(idOnibus).setPosition(latLng);
        }
    }

    //fechar alert Dialog
    private void tryDismiss() {
        try {
            if (alerta != null) {
                alerta.cancel();
                alerta.dismiss();
            }
        } catch (IllegalArgumentException ex) {
            Log.e("Teste", ex.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        apresentar();
    }

    //quando sai da tela primeiro é feito o onPause e depois o onDestroy
    @Override
    protected void onDestroy() {
        tryDismiss();
        try {
            handler.removeCallbacks(location);//parar de executar o handler, ou seja, parar de fazer as solicitações ao servidor
        } catch (Exception e) {
            Log.i("Teste", "Erro:" + e.getMessage());
        }
        super.onDestroy();
    }
}