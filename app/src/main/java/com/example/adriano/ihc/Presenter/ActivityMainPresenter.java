package com.example.adriano.ihc.Presenter;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.adriano.ihc.Activity.ActivityMain;
import com.example.adriano.ihc.Activity.ActivityMapa;
import com.example.adriano.ihc.Controller.CalcCoordenadas;
import com.example.adriano.ihc.Controller.ComunicaServidor;
import com.example.adriano.ihc.Controller.Localizacao;
import com.example.adriano.ihc.Controller.RetrofitInterface;
import com.example.adriano.ihc.Model.Atualizacao;
import com.example.adriano.ihc.Model.PontoParada;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by adriano on 30/10/16.
 */

public class ActivityMainPresenter {
    private ActivityMain activityMain;
    private Handler handler;
    private final int UPDATE_INTERVAL = 10000; /* milliseconds */
    private boolean gps_enabled, isConnected;
    private boolean flagServicoMandarlocalizacao;
    private LocationManager lm;
    private ConnectivityManager cm;
    private ComunicaServidor comunicacaoServidor;
    private long time;
    private Localizacao localizacao;
    private int cont, tentativaMandarLocalizacao;
    private SimpleDateFormat ft;
    private String code[], mylocation;


    public ActivityMainPresenter(ActivityMain activityMain) {
        this.activityMain = activityMain;

        lm = (LocationManager) activityMain.getSystemService(Context.LOCATION_SERVICE);
        cm = (ConnectivityManager) activityMain.getSystemService(Context.CONNECTIVITY_SERVICE);

        cont = 0;
        tentativaMandarLocalizacao = 0;

        ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        localizacao = new Localizacao(activityMain);
        localizacao.start();

        comunicacaoServidor = new ComunicaServidor(activityMain);
    }

    public String getMyLocation() {
        return localizacao.getLocalAtual();

    }

    public void fazerLeituraQrCode() {
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception ex) {
            Log.e("Teste", "Erro em ActivityMain >>:" + ex.getMessage());
        }
        //fazendo as verificações de conexão com a internet e localização
        if (!gps_enabled) { //se o gps estiver desativado
            activityMain.exibirToast("Não é possível obter sua localização atual!");
        } else if (!isConnected) {//se o usuario nao estiver conectado a internet
            activityMain.exibirToast("Por vafor verifique sua conexão com a internet!");
        } else { //se estiver tudo ok, fazer a leitura do QrCode
            if (!flagServicoMandarlocalizacao) {
                activityMain.ScanQrCode();
            } else {
                activityMain.exibirToast("Ops, você já fez a leitura do QrCode. Primeiro escolha a opção descer do ônibus ;)");
            }
        }
    }

    //metodo para mandar a localizacao atual do usuario para o servidor
    private void setLocation(String location) {
        long time2 = new Date().getTime();
        long diff = time2 - time;
        //calculando o delta T (diferença de tempo)
        long diffSeconds = (diff / 1000) % 60;
        time = time2;
        if (cont == 0 || !mylocation.equalsIgnoreCase(location)) {
            //calculando a distância percorrida em metros
            double distancia = CalcCoordenadas.distancia(location, mylocation);
            String dist = String.valueOf(distancia);

            mylocation = location; //atualizando a referencia do mylocation

            //calculando a velocidade
            double deltaV = distancia / diffSeconds;
            deltaV *= 3.6; //transformando em km/h
            String velocidade = String.valueOf(deltaV);

//        if (distancia >= 10 && deltaV >= 10) {
            String DateToStr = ft.format(new Date());
            Atualizacao atualizacao = new Atualizacao(code[0], cont, mylocation, DateToStr, dist, velocidade);
            comunicacaoServidor.mandarLocalizacao(atualizacao);
//        } else if (distancia <= 1) { // usuario deve estar parado
//            Toast.makeText(ActivityMain.this, "Ops, parece que você está no mesmo lugar.", Toast.LENGTH_SHORT).show();
//            tentativaMandarLocalizacao++;
//        } else if (deltaV < 10) { //usuario deve estar a pé
//            Toast.makeText(ActivityMain.this, "Ops, parece que você está andando a pé.", Toast.LENGTH_SHORT).show();
//            tentativaMandarLocalizacao++;
//        }
            //só para testes
            Log.i("Teste", " --- a localizacao foi alterada --- :" + location);/////////////////
//            Log.i("Teste", "atualizou em >>:" + diffSeconds + " segundos");//////////
//            Log.i("Teste", "distancia percorrida >>:" + distancia);/////////////////
//            Log.i("Teste", "velocidade >>:" + deltaV + " km/h");//////////
        } else {
            tentativaMandarLocalizacao++;
            //só para testes
            Log.i("Teste", " --- mesmo lugar  --- :" + location);/////////////////
//            Log.i("Teste", "atualizou em >>:" + diffSeconds + " segundos");//////////
        }

        //        if (tentativaMandarLocalizacao >= 30) { //algo esta errado parar de mandar a localizacao para o servidor
//            tentativaMandarLocalizacao = 0;
//            pararServicoMandarLocalizacao();
//        }
    }

    public void iniciarServicoMandarLocalizacao() {
        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(location, UPDATE_INTERVAL);
            time = new Date().getTime();
            mylocation = new String(localizacao.getLocalAtual());
            flagServicoMandarlocalizacao = true;
        }
    }

    //responsável por enviar localização do ônibus de tempos em tempos
    Runnable location = new Runnable() {
        @Override
        public void run() {
            String location = new String(localizacao.getLocalAtual());
            setLocation(location);
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    public void pararServicoMandarLocalizacao() {
        if (handler != null) {
            handler.removeCallbacks(location);
            handler = null;
        }
        flagServicoMandarlocalizacao = false;
        cont = 0;
        activityMain.setText_linha("");
        activityMain.exibirToast("Ok, obrigado pela sua colaboração! :)");
    }

    public void pararServicoAtualizarLocalizacao() {
        localizacao.stop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //no QrCode string com: idOnibus,linha
        String result = data.getStringExtra("SCAN_RESULT");
        code = result.split(",");
        activityMain.setText_linha("Abordo:" + code[1]);
        getPontos(code[1]);
    }

    public void atualizarContador() {
        cont++;
    }

    private void getPontos(String linha) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.Mockap)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<List<PontoParada>> call = retrofitInterface.getPontosParada(linha);
        call.enqueue(new Callback<List<PontoParada>>() {
            @Override
            public void onResponse(Call<List<PontoParada>> call, Response<List<PontoParada>> response) {
                int code = response.code();
                if (code == 200) {
                    List<PontoParada> lista = response.body(); //resposta do servidor
                    List<String> pontos = new ArrayList<String>();
                    for (PontoParada p : lista) {
                        pontos.add(new String(p.getDescricao()));
                    }
                    activityMain.mostrarPopupOpcoesDePontodeParada(pontos);
                } else { //caso a resposta nao seja 200 ok
                    activityMain.exibirToast("Falha na comunicação com servidor");
                }
            }

            //caso o aparelho esteja sem conexao com a internet
            @Override
            public void onFailure(Call<List<PontoParada>> call, Throwable t) {
                activityMain.exibirToast("Erro na conexão com a internet");
            }
        });
    }

}
