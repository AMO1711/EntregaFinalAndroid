package com.example.entregafinalandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private boolean isLogged = false;
    private String player;
    private TextView textoUsuario;
    private Button botonInicioCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textoUsuario = findViewById(R.id.textoNombreUsuario);
        botonInicioCerrarSesion = findViewById(R.id.botonInicioCerrarSesion);
    }

    public void jugar2048 (View view){
        //TODO Cambiar de Activity
    }

    public void jugarGiravoltorb (View view){
        //TODO Cambiar de Activity
    }

    public void verPuntuaciones (View view){
        //TODO Cambiar de Activity
    }

    public void inicioCerrarSesion (View view){
        //TODO crear inicio sesion
        if (isLogged){
            //Cerrar sesion
        }else {
            //Iniciar sesion
        }
    }
}