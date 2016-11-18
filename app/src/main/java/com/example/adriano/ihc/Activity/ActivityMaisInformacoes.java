package com.example.adriano.ihc.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import com.example.adriano.ihc.Model.Informacao;
import com.example.adriano.ihc.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityMaisInformacoes extends AppCompatActivity {

    private ProgressBar progressBar;
    private LinearLayout layout;
    private ListView listView;
    private ArrayList<Informacao> arrayList;
    private ArrayAdapter<Informacao> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mais_informacoes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //para aparececer o botao de voltar

        layout = (LinearLayout) findViewById(R.id.layoutMaisInformacoes);
        progressBar = (ProgressBar) findViewById(R.id.progressMaisInformacoes);
        listView = (ListView) findViewById(R.id.list_maisInformacoes);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = arrayList.get(position).getLink();
                mostrarSite(link);
            }
        });

        if(getIntent().getExtras().containsKey("cidade")){
            String cidade = getIntent().getExtras().getString("cidade");
            new ComunicaServidor(ActivityMaisInformacoes.this).getInformacoes(cidade);
        }

    }


    public void mostrarEmpresas(List<Informacao> lista) {
        arrayList = new ArrayList<Informacao>(lista);
        adapter = new MyListAdapter();
        listView.setAdapter(adapter);
        layout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void mostrarSite(String link) {
        //para testar se o usuário está conectado a internet
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            try {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(myIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Ops ocorreu uma falha ao abrir o navegador", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        } else {
            Toast.makeText(ActivityMaisInformacoes.this, "Por favor verifique sua conexão com a Internet!", Toast.LENGTH_LONG).show();
        }
    }


    private class MyListAdapter extends ArrayAdapter<Informacao> {
        public MyListAdapter() {
            super(ActivityMaisInformacoes.this, R.layout.item_list_mais_informacoes, arrayList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_list_mais_informacoes, parent, false);
            }

            String texto = arrayList.get(position).getEmpresa();

            TextView textView = (TextView) itemView.findViewById(R.id.textEmpresa);
            textView.setText(texto);

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
