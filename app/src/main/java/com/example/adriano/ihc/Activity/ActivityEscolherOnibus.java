package com.example.adriano.ihc.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adriano.ihc.Controller.ComunicaServidor;
import com.example.adriano.ihc.Model.PontoParada;
import com.example.adriano.ihc.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityEscolherOnibus extends AppCompatActivity {
    private ListView list_onibus;
    private ProgressBar progressBar;
    private LinearLayout layout;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private String linha, cidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolher_onibus);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras().containsKey("cidade")) {
            cidade = getIntent().getExtras().getString("cidade");
        }

        layout = (LinearLayout) findViewById(R.id.layoutEscolhaLinha);
        layout.setVisibility(View.INVISIBLE);//começa com o a lista e textView escondidos
        progressBar = (ProgressBar) findViewById(R.id.progressEscolherOnibus);
        list_onibus = (ListView) findViewById(R.id.list_onibus);

        buscarLinhas();

        list_onibus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                linha = arrayList.get(position);
                mostarMapa();
            }
        });
    }

    private void buscarLinhas() {
        new ComunicaServidor(ActivityEscolherOnibus.this).getLinhas(cidade);
    }

    public void exibirOpcaoTentarNovamente(){
        Snackbar snackbar = Snackbar
                .make(layout, "Ops ocorreu uma falha", Snackbar.LENGTH_INDEFINITE)
                .setAction("Tentar Novamente", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buscarLinhas();
                    }
                });
        snackbar.show();
    }

    public void mostrarLinhas(List<String> lista) {
        arrayList = new ArrayList<String>(lista);
        adapter = new MyListAdapter();
        list_onibus.setAdapter(adapter);
        layout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void mostarMapa() {
        //para testar se o usuário está conectado a internet
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            Intent it = new Intent(ActivityEscolherOnibus.this, ActivityMapa.class);
            it.putExtra("Linha",linha);
            startActivity(it);
        } else {
            Toast.makeText(ActivityEscolherOnibus.this, "Por favor verifique sua conexão com a Internet!", Toast.LENGTH_LONG).show();
        }
    }

    private class MyListAdapter extends ArrayAdapter<String> {
        public MyListAdapter() {
            super(ActivityEscolherOnibus.this, R.layout.item_list, arrayList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_list, parent, false);
            }

            String nome = arrayList.get(position);

            TextView linha = (TextView) itemView.findViewById(R.id.textView3);
            linha.setText(nome);

            return itemView;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
