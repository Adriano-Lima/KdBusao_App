package com.example.adriano.ihc.Activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.adriano.ihc.Controller.ComunicaServidor;
import com.example.adriano.ihc.Controller.Localizacao;
import com.example.adriano.ihc.Model.Bus;
import com.example.adriano.ihc.Model.Empresa;
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

public class ActivityMapa extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private LinearLayout layout;
    private GoogleMap mMap;
    private HashMap<Integer, Marker> markerHash;
    private PolylineOptions rectOptions;
    private Polyline polyline;
    private Handler handler;
    private int UPDATE_INTERVAL, contador;
    private String nmLinha, cidade, coordenadas;
    private AlertDialog alerta = null;
    private ComunicaServidor comunicaServidor;
    private List<PontoParada> pontosParada;
    private List<Empresa> empresas;
    private Localizacao localizacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        layout = (LinearLayout) findViewById(R.id.layoutMapa);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        UPDATE_INTERVAL = sharedPreferences.getInt("TempoAtualizacao", 10);
        UPDATE_INTERVAL *= 1000; //convertendo para milisegundos

        handler = new Handler();
        handler.postDelayed(location, UPDATE_INTERVAL);

        markerHash = null;

        if (getIntent().getExtras().containsKey("Linha"))
            nmLinha = getIntent().getExtras().getString("Linha", "");
        if (getIntent().getExtras().containsKey("cidade"))
            cidade = getIntent().getExtras().getString("cidade", "");
        if (getIntent().getExtras().containsKey("coordenadas"))
            coordenadas = getIntent().getExtras().getString("coordenadas", "");

        comunicaServidor = new ComunicaServidor(ActivityMapa.this);

        localizacao = new Localizacao(this);

        mostrarSnackBar("Vamos mostar a localização da linha: " + nmLinha);

        contador = 0;

    }

    public void mostrarSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void apresentar() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true); //apresentar a localização do usuário
        }
        buscarPontosParada();
        buscarEmpresas();
    }

    private void buscarPontosParada() {
        comunicaServidor.getPontos(nmLinha);
    }

    private void buscarEmpresas() {
        comunicaServidor.getEmpresas(cidade, coordenadas);
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

    public void exibirEmpresas(List<Empresa> empresas) {
        this.empresas = empresas;
        for (Empresa e : empresas) {
            LatLng latLng = new LatLng(e.getLatitude(), e.getLongitude());
            byte[] bytes = Base64.decode(e.getIcon(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            if (bitmap != null) {
                mMap.addMarker(new MarkerOptions().position(latLng).title(e.getNome()).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
            } else {
                Log.e("Teste", "erro em exibirEmpresas, bitmap é null");
            }
        }
    }



    //metodo chamado quando o usuario clica em um marker de uma empresa
    private void mostrarPopupDadosdaEmpresa(String nome, String descricao, String endereco, Bitmap bitmap) {


        LayoutInflater li = getLayoutInflater();

        View view = li.inflate(R.layout.dialog_dados_empresa, null);
        ImageView img = (ImageView) view.findViewById(R.id.imgEmpresa);
        TextView textNomeEmpresa, textEnderecoEmpresa, textDescricaoEmpresa;
        textNomeEmpresa = (TextView) view.findViewById(R.id.textNomeEmpresa);
        textEnderecoEmpresa = (TextView) view.findViewById(R.id.textEnderecoEmpresa);
        textDescricaoEmpresa = (TextView) view.findViewById(R.id.textDescricaoEmpresa);

        textNomeEmpresa.setText(nome);
        textDescricaoEmpresa.setText(descricao);
        textEnderecoEmpresa.setText(endereco);
        img.setImageBitmap(bitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(true);

        alerta = builder.create();
        alerta.show();
    }

    //responsável por fazer as solicitações ao servidor sobre a localização do ônibus
    Runnable location = new Runnable() {
        @Override
        public void run() {
            comunicaServidor.getLocalizacaoLinha(nmLinha);
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    //metodo responsavel por plotar no mapa a localizacao dos onibus
    public void setLocalizacaoDosOnibus(List<Bus> lista) {
        if (markerHash == null) {
            markerHash = new HashMap<>(lista.size());
        }
        for (Bus bus : lista) {
            updateMarker(bus.getId(), bus.getLat(), bus.getLongi(), bus.getSentido());
        }
    }

    //responsável por atualizar a posição do Marker no mapa(localização do ônibus)
    private void updateMarker(int idOnibus, double x, double y, String sentido) {
        LatLng latLng = new LatLng(x, y);
        if (!markerHash.containsKey(idOnibus)) {
            Marker marker;
            if (sentido.equals(pontosParada.get(0).getDescricao())) {
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(nmLinha).snippet("sentido " + sentido).icon(BitmapDescriptorFactory.fromResource(R.drawable.busazul)));
            } else {
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(nmLinha).snippet("sentido " + sentido).icon(BitmapDescriptorFactory.fromResource(R.drawable.buslaranja)));
            }
            marker.showInfoWindow(); //para deixar o título sempre visível
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            markerHash.put(idOnibus, marker);
        } else {
            markerHash.get(idOnibus).setPosition(latLng);
            markerHash.get(idOnibus).setSnippet("sentido " + sentido);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        apresentar();
    }

    //quando sai da tela primeiro é feito o onPause e depois o onDestroy
    @Override
    protected void onDestroy() {
        try {
            handler.removeCallbacks(location);//parar de executar o handler, ou seja, parar de fazer as solicitações ao servidor
        } catch (Exception e) {
            Log.i("Teste", "Erro:" + e.getMessage());
        }
        super.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        for (Empresa e : empresas) {
            if(marker.getTitle().equals(e.getNome())){
                byte[] bytes = Base64.decode(e.getImagem(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mostrarPopupDadosdaEmpresa(e.getNome(), e.getDescricao(), e.getEndereco(), bitmap);
            }
        }

        return false;
    }
}