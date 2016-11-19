package com.example.adriano.ihc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adriano.ihc.Controller.Localizacao;
import com.example.adriano.ihc.Presenter.ActivityMainPresenter;
import com.example.adriano.ihc.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.List;

public class ActivityMain extends AppCompatActivity

        implements NavigationView.OnNavigationItemSelectedListener {
    private RelativeLayout layout;
    private TextView text_linha, textMsgLocalizacao;
    private String code[], pontoDeParada;
    private AlertDialog alerta;
    private Localizacao localizacao;
    private ActivityMainPresenter presenter;
    private ListView listView;
    private ProgressBar progressBar;
    private ImageView imgMain;
    pl.droidsonroids.gif.GifTextView gif;

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

        layout = (RelativeLayout)findViewById(R.id.layoutMain);
        imgMain = (ImageView)findViewById(R.id.imgMain);
        imgMain.setImageResource(R.drawable.inicial);

        text_linha = (TextView) findViewById(R.id.text_linha);
        textMsgLocalizacao = (TextView)findViewById(R.id.textMsgLocalizacao);
        textMsgLocalizacao.setVisibility(View.INVISIBLE);

        gif = (pl.droidsonroids.gif.GifTextView)findViewById(R.id.gifMain);
        gif.setVisibility(View.INVISIBLE);

        localizacao = new Localizacao(ActivityMain.this);

        presenter = new ActivityMainPresenter(ActivityMain.this);
    }

    //só para testes
    public void escreverNoLog(String log){
        presenter.escreverNoLog(log);
    }

    public void exibirToast(String msg) {
        Toast.makeText(ActivityMain.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void TentarNovamenteBuscarPontosDeParada(){
        final int duracao = 5000;
        Snackbar snackbar = Snackbar
                .make(layout, "Ops ocorreu uma falha", Snackbar.LENGTH_LONG)
                .setAction("Tentar Novamente", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.buscarPontosdeParada();
                    }
                });
        snackbar.show();
    }

    public void setText_linha(String text) {
        text_linha.setText(text);
    }

    public void atualizarCont() {
        presenter.atualizarContador();
    }

    public void mostrarImagem(){
        gif.setVisibility(View.INVISIBLE);
        imgMain.setVisibility(View.VISIBLE);
        textMsgLocalizacao.setVisibility(View.INVISIBLE);
    }

    public void esconderImagem(){
        gif.setVisibility(View.VISIBLE);
        imgMain.setVisibility(View.INVISIBLE);
        textMsgLocalizacao.setVisibility(View.VISIBLE);
    }

    //metodo chamado depois que o usuario faz o scanner do Qrcode
    public void mostrarPopupOpcoesDePontodeParada() {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_opcoes_parada, null);
        listView = (ListView) view.findViewById(R.id.listaOpcoesPontoParada);
        listView.setVisibility(View.INVISIBLE);
        progressBar = (ProgressBar) view.findViewById(R.id.progressOpcoesParada);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setCancelable(false);

        alerta = builder.create();
        alerta.show();
    }

    public void fecharPopupOpcoesDePontodeParada(){
        alerta.dismiss();
    }

    public void mostrarListaOpcoesPontosDeParada(List<String> pontos){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, pontos);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                pontoDeParada = (String) adapter.getItemAtPosition(position);
                Toast.makeText(ActivityMain.this, "Ok, você irá descer no ponto: " + pontoDeParada, Toast.LENGTH_SHORT).show();
                fecharPopupOpcoesDePontodeParada();
                presenter.iniciarServicoMandarLocalizacao();
            }
        });
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
            presenter.onActivityResult(requestCode, resultCode, data);
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
                it.putExtra("coordenadas", location);
                it.putExtra("cidade", cidade);
                startActivity(it);
            } catch (Exception e) {
                Log.e("Teste", "Error em onNavigationItemSelected >>>:" + e.getMessage());
            }
        } else if (id == R.id.qrCode) {
            presenter.fazerLeituraQrCode();
        } else if (id == R.id.descerBus) {
            presenter.setFlag(false);
            presenter.pararServicoMandarLocalizacao();
        } else if (id == R.id.maisInformacoes) {
            Intent it = new Intent(ActivityMain.this, ActivityMaisInformacoes.class);
            String location = new String(presenter.getMyLocation());
            String cidade = localizacao.buscar("cidade", location);
            it.putExtra("cidade", cidade);
            startActivity(it);
        } else if (id == R.id.sobreNos) {
            startActivity(new Intent(ActivityMain.this, ActivitySobreNos.class));
        } else if (id == R.id.configuracoes) {
            startActivity(new Intent(ActivityMain.this, ActivityConfiguracoes.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        presenter.pararServicoMandarLocalizacao();
        presenter.pararServicoAtualizarLocalizacao();
        super.onDestroy();
    }
}
