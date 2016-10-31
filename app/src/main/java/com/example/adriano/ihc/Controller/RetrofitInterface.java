package com.example.adriano.ihc.Controller;

import com.example.adriano.ihc.Model.Atualizacao;
import com.example.adriano.ihc.Model.Bus;
import com.example.adriano.ihc.Model.PontoParada;
import com.example.adriano.ihc.Model.Resposta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {
    String Mockap = "http://192.168.0.13:8080";
    //String Mockap = "http://10.15.44.123:8080";

//    @GET("/user")
//    Call<User> getUser();
//
//    @GET("/parcelas")
//    Call<OpcoesdePagamento> getOpcoes();
//
    @POST("/bus/onibus/posicao")
    Call<Resposta> setLocation(@Body Atualizacao atualizacao);

    @GET("/bus/onibus/{linha}")
    Call<List<Bus>> getListOnibus(@Path("linha") String linha);

    @GET("/bus/linhas/{cidade}")
    Call<List<String>> getLinhasCidade(@Path("cidade") String cidade);

    @GET("/pontosParada/{linha}")
    Call<List<PontoParada>> getPontosParada(@Path("linha") String linha);
}
