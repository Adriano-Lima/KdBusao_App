package com.example.adriano.ihc.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adriano.ihc.R;

public class ActivityConfiguracoes extends AppCompatActivity {

    private SeekBar seekBarAtualicao, seekBarEnviarLocalizacao;
    private TextView textAtualizacao, textEnviarLocalizacao;
    private SharedPreferences sharedPreferences;
    private int progAtualizacao, progEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //para aparececer o botao de voltar

        seekBarAtualicao = (SeekBar) findViewById(R.id.seekBarAtualizacao);
        seekBarEnviarLocalizacao = (SeekBar) findViewById(R.id.seekBarEnviarLocalizacao);
        textAtualizacao = (TextView) findViewById(R.id.text_atualizacao);
        textEnviarLocalizacao = (TextView) findViewById(R.id.text_enviar_localizacao);

        seekBarAtualicao.setMax(50);
        seekBarEnviarLocalizacao.setMax(50);

        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        progAtualizacao = sharedPreferences.getInt("TempoAtualizacao", 10);
        progEnviar = sharedPreferences.getInt("TempoEnviarLocalizacao", 10);

        seekBarAtualicao.setProgress(progAtualizacao);
        seekBarEnviarLocalizacao.setProgress(progEnviar);
        textAtualizacao.setText("Atualizar o mapa de " + progAtualizacao + " em " + progAtualizacao + " segundos");
        textEnviarLocalizacao.setText("Enviar localização de " + progEnviar + " em " + progEnviar + " segundos");

        seekBarAtualicao.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progAtualizacao = progress;
                textAtualizacao.setText("Atualizar o mapa de " + progress + " em " + progress + " segundos");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textAtualizacao.setText("Atualizar o mapa de " + progAtualizacao + " em " + progAtualizacao + " segundos");
            }
        });

        seekBarEnviarLocalizacao.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progEnviar = progress;
                textEnviarLocalizacao.setText("Enviar localização de " + progress + " em " + progress + " segundos");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textEnviarLocalizacao.setText("Enviar localização de " + progEnviar + " em " + progEnviar + " segundos");
            }
        });

    }//fim do onCreate


    @Override
    public void finish() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("TempoAtualizacao",progAtualizacao);
        editor.putInt("TempoEnviarLocalizacao",progEnviar);
        editor.commit();
        Toast.makeText(ActivityConfiguracoes.this,"Configurações salvas",Toast.LENGTH_SHORT).show();
        super.finish();
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
