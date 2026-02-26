package com.example.entregafinalandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private boolean isLogged = false;
    private String player;
    private final String PLAYER_KEY = "jugador";
    private TextView textoUsuario;
    private Button botonInicioCerrarSesion;
    private DBHelper db;

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
        db = new DBHelper(this);
    }

    public void jugar2048 (View view){
        Intent intent = new Intent(this, Game2048Activity.class);
        intent.putExtra(PLAYER_KEY, player);
        startActivity(intent);

    }

    public void jugarGiravoltorb (View view){
        Intent intent = new Intent(this, GameGiravoltorbActivity.class);
        intent.putExtra(PLAYER_KEY, player);
        startActivity(intent);
    }

    public void verPuntuaciones (View view){
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }

    public void inicioCerrarSesion (View view){
        AlertDialog.Builder builder;
        View dialogView;
        if (isLogged){
            //Cerrar sesion
            builder = new AlertDialog.Builder(this);
            dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cerrar_sesion, null);
            builder.setView(dialogView);

            builder.setTitle("Advertencia");

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            builder.setPositiveButton("Estoy seguro", (dialog, which) -> {
                player = null;
                isLogged = false;
                textoUsuario.setText("Invitado");
                botonInicioCerrarSesion.setText("Iniciar sesión");
                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }else {
            //Iniciar sesion
            builder = new AlertDialog.Builder(this);
            dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_iniciar_sesion, null);
            builder.setView(dialogView);

            builder.setTitle("Inicio de sesión");

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            builder.setPositiveButton("Iniciar sesión", (dialog, which) -> {
                EditText texto_usuario = dialogView.findViewById(R.id.edit_nombre_usuario_dialog);
                EditText texto_contrasena = dialogView.findViewById(R.id.edit_contrasena_dialog);
                String usuario = String.valueOf(texto_usuario.getText()),
                        contrasena = String.valueOf(texto_contrasena.getText());

                Boolean isValid = db.checkLogIn(usuario, contrasena);

                if (isValid){
                    player = usuario;
                    isLogged = true;
                    textoUsuario.setText(player);
                    botonInicioCerrarSesion.setText("Cerrar sesión");
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    View dialogView2 = LayoutInflater.from(this).inflate(R.layout.dialog_usuario_contrasena_incorrecto, null);
                    builder2.setView(dialogView2);

                    builder2.setTitle("Advertencia");

                    builder2.setNegativeButton("OK", (dialog2, which2) -> dialog2.dismiss());

                    AlertDialog dialog2 = builder2.create();
                    dialog2.show();
                }

                dialog.dismiss();
            });

            builder.setNeutralButton("Crear usuario", (dialog, which) -> {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                View dialogView2 = LayoutInflater.from(this).inflate(R.layout.dialog_crear_usuario, null);
                builder2.setView(dialogView2);

                builder2.setTitle("Creación de un nuevo usuario");

                builder2.setNegativeButton("Cancelar", (dialog2, which2) -> dialog2.dismiss());

                builder2.setPositiveButton("Crear usuario", (dialog2, which2) -> {
                    EditText texto_nuevo_usuario = dialogView2.findViewById(R.id.edit_nuevo_nombre_usuario_dialog);
                    EditText texto_nueva_contrasena = dialogView2.findViewById(R.id.edit_nueva_contrasena_dialog);
                    String nuevo_usuario = String.valueOf(texto_nuevo_usuario.getText()),
                            nueva_contrasena = String.valueOf(texto_nueva_contrasena.getText());
                    db.addUser(nuevo_usuario, nueva_contrasena);
                    dialog2.dismiss();
                });

                AlertDialog dialog2 = builder2.create();
                dialog2.show();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}