package com.example.entregafinalandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import java.util.Random;

public class GameGiravoltorbActivity extends AppCompatActivity {
    private static int numPantallas;
    private static final String GAME = "Giravoltorb";
    private final String PLAYER_KEY = "jugador";
    private static String player;
    private int [][] tablero = new int[5][5], tableroRevelado = new int[5][5];
    private static DBHelper db;
    private static Timer timer;
    private static TextView timerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.game_giravoltorb);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        numPantallas = 0;

        player = getIntent().getStringExtra(PLAYER_KEY);
        if (player == null){
            player = "Invitado";
        }
        timerView = findViewById(R.id.time);
        db = new DBHelper(this);

        inicializar();
    }

    public void inicializar(){
        AlertDialog.Builder builder;
        View dialogView;

        builder = new AlertDialog.Builder(this);
        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_timer, null);
        builder.setView(dialogView);

        builder.setTitle("Nuevo juego " + player);
        builder.setCancelable(false);

        builder.setPositiveButton("Temporizador", (dialog, which) -> {
            timer = new Timer("temporizador", "Giravoltorb", this);
            final Integer[] minutosSeleccionados = new Integer[1];
            Integer [] posiblesMinutos = {3, 5, 10};

            AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            View dialogView2 = LayoutInflater.from(this).inflate(R.layout.dialog_timer_minutes, null);
            builder2.setView(dialogView2);

            builder2.setCancelable(false);

            builder2.setPositiveButton("Empezar", (dialog2, which2) -> {
                timer.setMaxTime(minutosSeleccionados[0] *60);
                new Thread(timer).start();

                dialog2.dismiss();
            });

            Spinner spinnerTimer = dialogView2.findViewById(R.id.spinner_timer_minutes);
            ArrayAdapter<Integer> adapterTimer = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, posiblesMinutos);
            adapterTimer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTimer.setAdapter(adapterTimer);

            spinnerTimer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    minutosSeleccionados[0] = posiblesMinutos[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    minutosSeleccionados[0] = posiblesMinutos[0];
                }
            });

            dialog.dismiss();

            AlertDialog dialog2 = builder2.create();
            dialog2.show();
        });

        builder.setNegativeButton("Cronómetro", (dialog, which) -> {
            timer = new Timer("cronometro", "Giravoltorb", this);
            new Thread(timer).start();
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        iniciarTablero();
    }

    private void iniciarListeners(){
        String nombre_casilla;
        TextView casilla;

        for (int i = 0; i < 25; i++) {
            nombre_casilla = "casilla" + i;
            casilla = findViewById(getResources().getIdentifier(nombre_casilla, "id", getPackageName()));

            final int indice = i;

            casilla.setOnClickListener(view -> {
                comprobarBomba(indice/5, indice%5);
            });
        }
    }

    private void comprobarBomba (int fila, int columna){
        if (tablero[fila][columna] == 0){
            gameOver(this);
            return;
        }

        tableroRevelado[fila][columna] = 1;
        mostrarTablero();
        comprobarTablero();
    }

    public void mostrarTablero (){
        String nombre_casilla;
        TextView casilla;

        for (int fila = 0; fila < tablero.length; fila++) {
            for (int columna = 0; columna < tablero[0].length; columna++) {
                nombre_casilla = "casilla" + (5*fila+columna);
                casilla = findViewById(getResources().getIdentifier(nombre_casilla, "id", getPackageName()));

                if (tableroRevelado[fila][columna] == 0){
                    casilla.setText("¿?");
                    casilla.setBackgroundColor(getColor(R.color.light_grey));
                } else {
                    casilla.setText(String.valueOf(tablero[fila][columna]));
                    switch (tablero[fila][columna]){
                        case 1:
                            casilla.setBackgroundColor(getColor(R.color.score2));
                            break;
                        case 2:
                            casilla.setBackgroundColor(getColor(R.color.score512));
                            break;
                        case 3:
                            casilla.setBackgroundColor(getColor(R.color.score16));
                            break;
                    }
                }

            }
        }
    }

    public void iniciarTablero(){
        int cantidadBombas, cantidad1, cantidad2, cantidad3, numRandom, bombasF0 = 0, bombasF1 = 0,
                bombasF2 = 0, bombasF3 = 0, bombasF4 = 0, puntosF0 = 0, puntosF1 = 0, puntosF2 = 0,
                puntosF3 = 0, puntosF4 = 0, bombasC0 = 0, bombasC1 = 0, bombasC2 = 0, bombasC3 = 0,
                bombasC4 = 0, puntosC0 = 0, puntosC1 = 0, puntosC2 = 0, puntosC3 = 0, puntosC4 = 0;
        int [] posibilidades = new int[25];
        Random random = new Random();
        ArrayList<Integer> numRandomSeleccionados = new ArrayList<>();
        TextView fila0, fila1, fila2, fila3, fila4, columna0, columna1, columna2, columna3, columna4;

        if (numPantallas < 3){
            cantidadBombas = 4;
            cantidad1 = 12;
            cantidad2 = 6;
            cantidad3 = 3;
        } else if (numPantallas < 6) {
            cantidadBombas = 6;
            cantidad1 = 7;
            cantidad2 = 7;
            cantidad3 = 5;
        } else {
            cantidadBombas = 10;
            cantidad1 = 2;
            cantidad2 = 8;
            cantidad3 = 5;
        }

        for (int i = 0; i < cantidadBombas; i++) {
            posibilidades [i] = 0;
        }

        for (int i = cantidadBombas; i < cantidadBombas+cantidad1; i++) {
            posibilidades [i] = 1;
        }

        for (int i = cantidadBombas+cantidad1; i < cantidadBombas+cantidad1+cantidad2; i++) {
            posibilidades [i] = 2;
        }

        for (int i = cantidadBombas+cantidad1+cantidad2; i < 25; i++) {
            posibilidades [i] = 3;
        }

        for (int fila = 0; fila < tableroRevelado.length; fila++) {
            for (int columna = 0; columna < tableroRevelado.length; columna++) {
                tableroRevelado[fila][columna] = 0;

                do {
                    numRandom = random.nextInt(25);
                } while (numRandomSeleccionados.contains(numRandom));

                numRandomSeleccionados.add(numRandom);

                tablero[fila][columna] = posibilidades[numRandom];

                if (tablero[fila][columna] == 0){
                    switch (fila){
                        case 0: bombasF0++; break;
                        case 1: bombasF1++; break;
                        case 2: bombasF2++; break;
                        case 3: bombasF3++; break;
                        case 4: bombasF4++; break;
                    }
                    switch (columna){
                        case 0: bombasC0++; break;
                        case 1: bombasC1++; break;
                        case 2: bombasC2++; break;
                        case 3: bombasC3++; break;
                        case 4: bombasC4++; break;
                    }
                } else {
                    switch (fila){
                        case 0: puntosF0+=tablero[fila][columna]; break;
                        case 1: puntosF1+=tablero[fila][columna]; break;
                        case 2: puntosF2+=tablero[fila][columna]; break;
                        case 3: puntosF3+=tablero[fila][columna]; break;
                        case 4: puntosF4+=tablero[fila][columna]; break;
                    }
                    switch (columna){
                        case 0: puntosC0+=tablero[fila][columna]; break;
                        case 1: puntosC1+=tablero[fila][columna]; break;
                        case 2: puntosC2+=tablero[fila][columna]; break;
                        case 3: puntosC3+=tablero[fila][columna]; break;
                        case 4: puntosC4+=tablero[fila][columna]; break;
                    }
                }
            }
        }

        fila0 = findViewById(R.id.fila0);
        fila1 = findViewById(R.id.fila1);
        fila2 = findViewById(R.id.fila2);
        fila3 = findViewById(R.id.fila3);
        fila4 = findViewById(R.id.fila4);
        columna0 = findViewById(R.id.columna0);
        columna1 = findViewById(R.id.columna1);
        columna2 = findViewById(R.id.columna2);
        columna3 = findViewById(R.id.columna3);
        columna4 = findViewById(R.id.columna4);

        fila0.setText("P" + puntosF0 + "B" + bombasF0);
        fila1.setText("P" + puntosF1 + "B" + bombasF1);
        fila2.setText("P" + puntosF2 + "B" + bombasF2);
        fila3.setText("P" + puntosF3 + "B" + bombasF3);
        fila4.setText("P" + puntosF4 + "B" + bombasF4);
        columna0.setText("P" + puntosC0 + "/B" + bombasC0);
        columna1.setText("P" + puntosC1 + "/B" + bombasC1);
        columna2.setText("P" + puntosC2 + "/B" + bombasC2);
        columna3.setText("P" + puntosC3 + "/B" + bombasC3);
        columna4.setText("P" + puntosC4 + "/B" + bombasC4);

        iniciarListeners();
        mostrarTablero();
    }

    public void comprobarTablero(){
        boolean isWin = true;

        for (int fila = 0; fila < tablero.length; fila++) {
            for (int columna = 0; columna < tablero[0].length; columna++) {
                if ((tablero[fila][columna] == 2 || tablero[fila][columna] == 3) &&
                        tableroRevelado[fila][columna] == 0){
                    isWin = false;
                }
            }
        }

        if (isWin){
            siguienteTablero();
        }
    }

    public void siguienteTablero(){
        AlertDialog.Builder builder;
        View dialogView;
        TextView viewNumPantallas = findViewById(R.id.score);

        numPantallas++;
        viewNumPantallas.setText(String.valueOf(numPantallas));
        iniciarTablero();

        builder = new AlertDialog.Builder(this);
        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_siguiente_pantalla, null);
        builder.setView(dialogView);

        builder.setTitle("Feliciades");

        builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void gameOver(Activity activity) {
        int tiempo;
        AlertDialog.Builder builder;
        View dialogView;
        TextView score, time, timeMode;

        if (timer.getTimeMode().equals("temporizador")){
            tiempo = timer.getMaxTime();
        } else {
            timer.stop();
            tiempo = timer.getSeconds();
        }

        db.addScore(GAME, player, numPantallas, tiempo, timer.getTimeMode());

        builder = new AlertDialog.Builder(activity);
        dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_resultados, null);
        builder.setView(dialogView);

        score = dialogView.findViewById(R.id.texto_resultados_puntuacion);
        time = dialogView.findViewById(R.id.texto_resultados_tiempo);
        timeMode = dialogView.findViewById(R.id.texto_resultados_time_mode);

        score.setText(String.valueOf(numPantallas));
        time.setText(tiempo/60 + ":" + tiempo%60);
        timeMode.setText(timer.getTimeMode());

        builder.setTitle("Resultados");
        builder.setCancelable(false);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            dialog.dismiss();
            activity.finish();
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        timer.stop();
        numPantallas = 0;
    }

    public static void updateTimer(String tiempo) {
        timerView.setText(tiempo);
    }

    public void volverPrincipal(View view){
        finish();
    }

    public void reiniciar(View view){
        inicializar();
    }
}
