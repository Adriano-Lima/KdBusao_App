package com.example.adriano.ihc.Controller;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.example.adriano.ihc.Activity.ActivityEscolherOnibus;
import com.example.adriano.ihc.Activity.ActivityMain;
import com.example.adriano.ihc.Activity.ActivityMaisInformacoes;
import com.example.adriano.ihc.Activity.ActivityMapa;
import com.example.adriano.ihc.Model.Atualizacao;
import com.example.adriano.ihc.Model.Bus;
import com.example.adriano.ihc.Model.Informacao;
import com.example.adriano.ihc.Model.PontoParada;
import com.example.adriano.ihc.Model.Resposta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ComunicaServidor {

    private Context context;
    private Resposta resposta;

    public ComunicaServidor(Context context) {
        this.context = context;
    }

    //mostra um Toast com uma dada mensagem
    private void mostrarAviso(final String msg) {
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        };
        mainHandler.post(myRunnable);
    }

    public void mandarLocalizacao(Atualizacao atualizacao) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.Mockap)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<Resposta> call = retrofitInterface.setLocation(atualizacao);
        call.enqueue(new Callback<Resposta>() {
            @Override
            public void onResponse(Call<Resposta> call, Response<Resposta> response) {
                int code = response.code();
                if (code == 200) {
                    resposta = response.body(); //resposta do servidor
                    ((ActivityMain) context).atualizarCont();
                    mostrarAviso("Localização envida ao servidor!");/////// só para testes
                } else { //caso a resposta nao seja 200 ok
                    mostrarAviso("Erro ao enviar a localização ao servidor");
                }
            }

            //caso o aparelho esteja sem conexao com a internet
            @Override
            public void onFailure(Call<Resposta> call, Throwable t) {
                mostrarAviso("Erro na conexão com a internet");
            }
        });
    }

    public void getLocalizacaoLinha(String linha) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.Mockap)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<List<Bus>> call = retrofitInterface.getListOnibus(linha);
        call.enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                int code = response.code();
                if (code == 200) {
                    List<Bus> lista = response.body(); //resposta do servidor
                    ((ActivityMapa) context).setLocalizacaoDosOnibus(lista);
                } else { //caso a resposta nao seja 200 ok
                    mostrarAviso("Falha na comunicação com servidor");
                }
            }

            //caso o aparelho esteja sem conexao com a internet
            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                mostrarAviso("Erro na conexão com a internet");
            }
        });
    }

    public void getPontos(String linha) {
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
                    ((ActivityMapa) context).exibirPontosParadaMapa(lista);
                } else { //caso a resposta nao seja 200 ok
                    mostrarAviso("Falha na comunicação com servidor");
                }
            }

            //caso o aparelho esteja sem conexao com a internet
            @Override
            public void onFailure(Call<List<PontoParada>> call, Throwable t) {
                mostrarAviso("Erro na conexão com a internet");
            }
        });
    }

    public void getLinhas(String cidade) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.Mockap)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<List<String>> call = retrofitInterface.getLinhasCidade(cidade);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                int code = response.code();
                if (code == 200) {
                    List<String> lista = response.body(); //resposta do servidor
                    ((ActivityEscolherOnibus) context).mostrarLinhas(lista);
                } else { //caso a resposta nao seja 200 ok
                    ((ActivityEscolherOnibus) context).exibirOpcaoTentarNovamente();
                }
            }

            //caso o aparelho esteja sem conexao com a internet
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                ((ActivityEscolherOnibus) context).exibirOpcaoTentarNovamente();
            }
        });
    }

    public void getInformacoes(String cidade) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.Mockap)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<List<Informacao>> call = retrofitInterface.getInformacoes(cidade);
        call.enqueue(new Callback<List<Informacao>>() {
            @Override
            public void onResponse(Call<List<Informacao>> call, Response<List<Informacao>> response) {
                int code = response.code();
                if (code == 200) {
                    List<Informacao> lista = response.body(); //resposta do servidor
                    ((ActivityMaisInformacoes) context).mostrarEmpresas(lista);
                } else { //caso a resposta nao seja 200 ok
                    mostrarAviso("Falha na comunicação com servidor");
                }
            }

            //caso o aparelho esteja sem conexao com a internet
            @Override
            public void onFailure(Call<List<Informacao>> call, Throwable t) {
                mostrarAviso("Erro na conexão com a internet");
            }
        });
    }


}
