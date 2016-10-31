package com.example.adriano.ihc.Activity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adriano.ihc.Controller.CalcCoordenadas;
import com.example.adriano.ihc.Controller.ComunicaServidor;
import com.example.adriano.ihc.Controller.Localizacao;
import com.example.adriano.ihc.Model.Atualizacao;
import com.example.adriano.ihc.Presenter.ActivityMainPresenter;
import com.example.adriano.ihc.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityMain extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView text_linha;
    private String code[], pontoDeParada;
    private AlertDialog alerta;
    private Localizacao localizacao;
    private ActivityMainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.fazerLeituraQrCode();
            }
        });

        localizacao = new Localizacao(ActivityMain.this);

        text_linha = (TextView) findViewById(R.id.text_linha);

        presenter = new ActivityMainPresenter(ActivityMain.this);
    }

    public void exibirToast(String msg){
        Toast.makeText(ActivityMain.this,msg,Toast.LENGTH_SHORT).show();
    }

    public void setText_linha(String text){
        text_linha.setText(text);
    }

    public void atualizarCont(){
        presenter.atualizarContador();
    }

    //metodo chamado depois que o usuario faz o scanner do Qrcode
    public void mostrarPopupOpcoesDePontodeParada(List<String> pontos) {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.my_dialog_opcoes_parada, null);
        ListView listView = (ListView) view.findViewById(R.id.listaOpcoesPontoParada);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, pontos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                pontoDeParada = (String) adapter.getItemAtPosition(position);
                Toast.makeText(ActivityMain.this, "Ok, você irá descer no ponto: " + pontoDeParada, Toast.LENGTH_SHORT).show();
                alerta.dismiss();
                presenter.iniciarServicoMandarLocalizacao();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);
//        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//                if (month.getValue() < 10) {
//                    String mes = "0" + (month.getValue());
//                    String ano = "" + year.getValue();
//                    edtValidade.setText(mes + "/" + ano);
//                } else {
//                    String mes = "" + (month.getValue());
//                    String ano = "" + year.getValue();
//                    edtValidade.setText(mes + "/" + ano);
//                }
//            }
//        });
//        builder.setNegativeButton(R.string.Cancelar,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface arg0, int arg1) {
//                    }
//                });
        alerta = builder.create();
        alerta.show();
    }

    //leitura do QrCode
    public void ScanQrCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Posicione o QR code");
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            presenter.onActivityResult(requestCode,resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.buscarBus) {
            try {
                Intent it = new Intent(ActivityMain.this, ActivityEscolherOnibus.class);
                String location = new String(presenter.getMyLocation());
                String cidade = localizacao.buscar("cidade", location);
                it.putExtra("cidade", cidade);
                startActivity(it);
            }catch (Exception e){
                Log.e("Teste","Error em onNavigationItemSelected >>>:"+e.getMessage());
            }
        }  else if (id == R.id.qrCode) {
            presenter.fazerLeituraQrCode();
        } else if (id == R.id.descerBus) {
            presenter.pararServicoMandarLocalizacao();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        presenter.pararServicoAtualizarLocalizacao();
        super.onDestroy();
    }
}
