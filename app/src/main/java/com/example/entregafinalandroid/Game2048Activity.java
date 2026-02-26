package com.example.entregafinalandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Game2048Activity extends AppCompatActivity {

    private static int high_score = 2;
    private int anterior_maximos = 3;
    private int [][] matriz_tablero = new int[4][4], matriz_anterior = new int[4][4];
    private GridLayout pantalla;
    private ImageButton botonAnterior;
    private static String player;
    private static final String GAME = "2048";
    private final String PLAYER_KEY = "jugador";
    private static DBHelper db;
    private static Timer timer;
    private static TextView timerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.game2048);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        timerView = findViewById(R.id.time);
        botonAnterior = findViewById(R.id.botonAnterior);
        player = getIntent().getStringExtra(PLAYER_KEY);

        if (player == null){
            player = "Invitado";
        }

        db = new DBHelper(this);

        GestureDetectorCompat gDetector = new GestureDetectorCompat(this, new MyGestureListener());
        pantalla = findViewById(R.id.cuadricula);

        pantalla.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gDetector.onTouchEvent(event);
            }
        });

        inicializar_matriz();
        siguiente();
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            float difX, difY;

            difX = e2.getX() - e1.getX();
            difY = e2.getY() - e1.getY();

            if (Math.abs(Math.abs(difX) - Math.abs(difY)) > 50){
                if (Math.abs(difX) > Math.abs(difY)){
                    if (difX > 0){
                        goRight();
                    } else {
                        goLeft();
                    }
                } else {
                    if (difY > 0){
                        goDown();
                    } else {
                        goUp();
                    }
                }
            }
            return true;
        }
    }

    public void goRight(){
        for (int i = 0; i < matriz_tablero.length; i++) {
            for (int j = 0; j < matriz_tablero.length; j++) {
                matriz_anterior[i][j] = matriz_tablero[i][j];
            }
        }

        for (int fila = 0; fila < matriz_tablero.length; fila++) {
            for (int columna = matriz_tablero.length-2; columna >= 0; columna--) {
                for (int iteraciones = 0; iteraciones < matriz_tablero.length-columna-1; iteraciones++) {
                    if (matriz_tablero[fila][columna+iteraciones+1] == 0){
                        matriz_tablero[fila][columna+iteraciones+1] = matriz_tablero[fila][columna+iteraciones];
                        matriz_tablero[fila][columna+iteraciones] = 0;
                    }

                    if (matriz_tablero[fila][columna+iteraciones+1] == matriz_tablero[fila][columna+iteraciones]){
                        matriz_tablero[fila][columna+iteraciones+1] *= 2;
                        matriz_tablero[fila][columna+iteraciones] = 0;
                    }
                }
            }
        }
        siguiente();
    }

    public void goLeft(){
        for (int i = 0; i < matriz_tablero.length; i++) {
            for (int j = 0; j < matriz_tablero.length; j++) {
                matriz_anterior[i][j] = matriz_tablero[i][j];
            }
        }

        for (int fila = 0; fila < matriz_tablero.length; fila++) {
            for (int columna = 1; columna < matriz_tablero.length; columna++) {
                for (int iteraciones = 0; iteraciones < columna; iteraciones++) {
                    if (matriz_tablero[fila][columna-iteraciones-1] == 0){
                        matriz_tablero[fila][columna-iteraciones-1] = matriz_tablero[fila][columna-iteraciones];
                        matriz_tablero[fila][columna-iteraciones] = 0;
                    }

                    if (matriz_tablero[fila][columna-iteraciones-1] == matriz_tablero[fila][columna-iteraciones]){
                        matriz_tablero[fila][columna-iteraciones-1] *= 2;
                        matriz_tablero[fila][columna-iteraciones] = 0;
                    }
                }
            }
        }
        siguiente();
    }

    public void goUp(){
        for (int i = 0; i < matriz_tablero.length; i++) {
            for (int j = 0; j < matriz_tablero.length; j++) {
                matriz_anterior[i][j] = matriz_tablero[i][j];
            }
        }

        for (int columna = 0; columna < matriz_tablero.length; columna++) {
            for (int fila = 1; fila < matriz_tablero.length; fila++) {
                for (int iteraciones = 0; iteraciones < fila; iteraciones++) {
                    if (matriz_tablero[fila-iteraciones-1][columna] == 0){
                        matriz_tablero[fila-iteraciones-1][columna] = matriz_tablero[fila-iteraciones][columna];
                        matriz_tablero[fila-iteraciones][columna] = 0;
                    }

                    if (matriz_tablero[fila-iteraciones-1][columna] == matriz_tablero[fila-iteraciones][columna]){
                        matriz_tablero[fila-iteraciones-1][columna] *= 2;
                        matriz_tablero[fila-iteraciones][columna] = 0;
                    }
                }
            }
        }
        siguiente();
    }

    public void goDown(){
        for (int i = 0; i < matriz_tablero.length; i++) {
            for (int j = 0; j < matriz_tablero.length; j++) {
                matriz_anterior[i][j] = matriz_tablero[i][j];
            }
        }

        for (int columna = 0; columna < matriz_tablero.length; columna++) {
            for (int fila = matriz_tablero.length-2; fila >= 0; fila--) {
                for (int iteraciones = 0; iteraciones < matriz_tablero.length-fila-1; iteraciones++) {
                    if (matriz_tablero[fila+iteraciones+1][columna] == 0){
                        matriz_tablero[fila+iteraciones+1][columna] = matriz_tablero[fila+iteraciones][columna];
                        matriz_tablero[fila+iteraciones][columna] = 0;
                    }

                    if (matriz_tablero[fila+iteraciones+1][columna] == matriz_tablero[fila+iteraciones][columna]){
                        matriz_tablero[fila+iteraciones+1][columna] *= 2;
                        matriz_tablero[fila+iteraciones][columna] = 0;
                    }
                }
            }
        }
        siguiente();
    }

    public void siguiente(){
        boolean continuar;

        actualizar_high_score();

        continuar = generar_numero();
        if (!continuar){
            gameOver(this);
        }

        mostrar_tablero();
    }

    public void mostrar_tablero (){
        String nombre_casilla;
        TextView casilla;

        for (int fila = 0; fila < matriz_tablero.length; fila++) {
            for (int columna = 0; columna < matriz_tablero.length; columna++) {
                nombre_casilla = "casilla" + (4*fila+columna);
                casilla = findViewById(getResources().getIdentifier(nombre_casilla, "id", getPackageName()));
                if (matriz_tablero[fila][columna] == 0){
                    casilla.setText(" ");
                } else {
                    casilla.setText(String.valueOf(matriz_tablero[fila][columna]));
                }

                switch (matriz_tablero[fila][columna]){
                    case 0:
                        casilla.setBackgroundColor(getColor(R.color.light_grey));
                        break;
                    case 2:
                        casilla.setBackgroundColor(getColor(R.color.score2));
                        break;
                    case 4:
                        casilla.setBackgroundColor(getColor(R.color.score4));
                        break;
                    case 8:
                        casilla.setBackgroundColor(getColor(R.color.score8));
                        break;
                    case 16:
                        casilla.setBackgroundColor(getColor(R.color.score16));
                        break;
                    case 32:
                        casilla.setBackgroundColor(getColor(R.color.score32));
                        break;
                    case 64:
                        casilla.setBackgroundColor(getColor(R.color.score64));
                        break;
                    case 128:
                        casilla.setBackgroundColor(getColor(R.color.score128));
                        break;
                    case 256:
                        casilla.setBackgroundColor(getColor(R.color.score256));
                        break;
                    case 512:
                        casilla.setBackgroundColor(getColor(R.color.score512));
                        break;
                    case 1024:
                        casilla.setBackgroundColor(getColor(R.color.score1024));
                        break;
                    default:
                        casilla.setBackgroundColor(getColor(R.color.scoreOver2048));
                }
            }
        }
    }

    public boolean generar_numero(){
        boolean lleno = true;
        int numero_aleatorio, fila, columna;

        for (int i = 0; i < matriz_tablero.length; i++) {
            for (int j = 0; j < matriz_tablero.length; j++) {
                if (matriz_tablero[i][j] == 0){
                    lleno = false;
                    break;
                }
            }
        }

        if (lleno){
            return false;
        }

        while (true){
            numero_aleatorio = (int) (Math.random()*16);
            fila = numero_aleatorio/matriz_tablero.length;
            columna =  numero_aleatorio%matriz_tablero.length;

            if (matriz_tablero[fila][columna] == 0){
                matriz_tablero[fila][columna] = 2;
                return true;
            }
        }
    }

    public void inicializar_matriz(){
        AlertDialog.Builder builder;
        View dialogView;

        for (int fila = 0; fila < matriz_tablero.length; fila++) {
            for (int columna = 0; columna < matriz_tablero.length; columna++) {
                matriz_tablero[fila][columna] = 0;
            }
        }

        builder = new AlertDialog.Builder(this);
        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_timer, null);
        builder.setView(dialogView);

        builder.setTitle("Nuevo juego " + player);
        builder.setCancelable(false);

        builder.setPositiveButton("Temporizador", (dialog, which) -> {
            timer = new Timer("temporizador", "2048", this);
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
            timer = new Timer("cronometro", "2048", this);
            new Thread(timer).start();
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void actualizar_high_score(){
        for (int fila = 0; fila < matriz_tablero.length; fila++) {
            for (int columna = 0; columna < matriz_tablero.length; columna++) {
                if (matriz_tablero[fila][columna] > high_score){
                    high_score = matriz_tablero[fila][columna];
                    TextView panel_high_score = findViewById(R.id.high_score);
                    panel_high_score.setText(String.valueOf(high_score));
                }
            }
        }

    }

    public static void gameOver(Activity activity){
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

        db.addScore(GAME, player, high_score, tiempo, timer.getTimeMode());

        builder = new AlertDialog.Builder(activity);
        dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_resultados, null);
        builder.setView(dialogView);

        score = dialogView.findViewById(R.id.texto_resultados_puntuacion);
        time = dialogView.findViewById(R.id.texto_resultados_tiempo);
        timeMode = dialogView.findViewById(R.id.texto_resultados_time_mode);

        score.setText(String.valueOf(high_score));
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

    }


    public void reiniciar(View view){
        inicializar_matriz();
        siguiente();
        anterior_maximos = 3;
        botonAnterior.setEnabled(true);
    }

    public void anterior (View view){
        if (anterior_maximos>0){
            for (int i = 0; i < matriz_tablero.length; i++) {
                for (int j = 0; j < matriz_tablero.length; j++) {
                    matriz_tablero[i][j] = matriz_anterior[i][j];
                }
            }
            anterior_maximos--;

            if (anterior_maximos == 0){
                botonAnterior.setEnabled(false);
            }

            mostrar_tablero();
        } else {
            Toast.makeText(this,"No te quedan backs", Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateTimer(String tiempo){
        timerView.setText(tiempo);
    }

    public void volverPrincipal(View view){
        finish();
    }
}
