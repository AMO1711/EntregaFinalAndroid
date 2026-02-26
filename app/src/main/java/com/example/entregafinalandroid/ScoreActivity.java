package com.example.entregafinalandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {
    TextView textoResultados;
    DBHelper db;
    Spinner spinnerFiltrar, spinnerFiltrar2, spinnerOrdenar;
    String [] filtro = {"Juego", "Jugador"}, ordenacion = {"Puntuación", "Tiempo"},
            juegos = {"2048", "Giravoltorb"};
    ArrayList<String> usuarios;
    String filtroActual, ordenacionActual, juegoActual, usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textoResultados = findViewById(R.id.texto_resultados);

        db = new DBHelper(this);

        spinnerFiltrar = findViewById(R.id.spinner_filtrar);
        spinnerFiltrar2 = findViewById(R.id.spinner_filtrar2);
        spinnerOrdenar = findViewById(R.id.spinner_ordenar);

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, filtro);
        ArrayAdapter<String> adapterOrdenacion = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, ordenacion);

        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterOrdenacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFiltrar.setAdapter(adapterFiltro);
        spinnerOrdenar.setAdapter(adapterOrdenacion);

        spinnerFiltrar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtroActual = filtro[position];

                if (filtroActual.equals("Juego")){
                    ArrayAdapter<String> adapterFiltroJuego = new ArrayAdapter<>(
                            view.getContext(), android.R.layout.simple_spinner_item, juegos);

                    adapterFiltroJuego.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerFiltrar2.setAdapter(adapterFiltroJuego);

                    spinnerFiltrar2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            juegoActual = juegos[position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            juegoActual = null;
                        }
                    });
                } else {
                    usuarios = db.getUsers();

                    ArrayAdapter<String> adapterFiltroUser = new ArrayAdapter<>(
                            view.getContext(), android.R.layout.simple_spinner_item, usuarios);

                    adapterFiltroUser.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerFiltrar2.setAdapter(adapterFiltroUser);

                    spinnerFiltrar2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            usuarioActual = usuarios.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            usuarioActual = null;
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filtroActual = null;
            }
        });

        spinnerOrdenar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ordenacionActual = ordenacion[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ordenacionActual = null;
            }
        });

    }

    public void buscar (View view){
        ArrayList<Score> puntuacionesFiltradasOrdenadas;
        String filtro, nombre, ordenacion1, ordenacion2, resultado = "";

        if (filtroActual.equals("Juego")){
            filtro = "game";
            nombre = juegoActual;
        } else {
            filtro = "playerName";
            nombre = usuarioActual;
        }

        if (ordenacionActual.equals("Puntuación")){
            ordenacion1 = "score";
            ordenacion2 = "time";
        } else {
            ordenacion1 = "time";
            ordenacion2 = "score";
        }

        puntuacionesFiltradasOrdenadas = db.getFilteredOrdenedScores(filtro, nombre, ordenacion1, ordenacion2);

        for (Score score:puntuacionesFiltradasOrdenadas){
            resultado += score.getPlayerName() + "\t" + score.getGame() + "\t" +
                    score.getScore() + "\t" + score.getTime()/60 + ":" + score.getTime()%60 + "\t" +
                    score.getTimeMode() + "\n";
        }

        textoResultados.setText(resultado);
    }

    public void volverPrincipal(View view){
        finish();
    }
}
