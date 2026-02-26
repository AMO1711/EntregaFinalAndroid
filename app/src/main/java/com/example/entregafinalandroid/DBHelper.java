package com.example.entregafinalandroid;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "finalDB";
    private final Context context;

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE scores (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "game TEXT, playerName TEXT, score TEXT, time TEXT, timeMode TEXT);");
        db.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "playerName TEXT UNIQUE, password TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS scores");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public void addScore (String game, String name, int score, int time, String timeMode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("game", game);
        cv.put("playerName", name);
        cv.put("score", score);
        cv.put("time", time);
        cv.put("timeMode", timeMode);
        db.insert("scores", null, cv);
        db.close();
    }

    public ArrayList<Score> getFilteredOrdenedScores (String filtro, String nombre, String ordenacion1, String ordenacion2){
        ArrayList<Score> puntuaciones = new ArrayList<>();
        Score puntuacionActual;
        String [] selectionArgs = {nombre};
        String query;

        if (ordenacion1.equals("score")){
            query = "SELECT * FROM scores WHERE " + filtro + "=? ORDER BY " + ordenacion1 + " DESC, " + ordenacion2;
        } else {
            query = "SELECT * FROM scores WHERE " + filtro + "=? ORDER BY " + ordenacion1 + ", " + ordenacion2 + " DESC";
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                do {
                    puntuacionActual = new Score();
                    puntuacionActual.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    puntuacionActual.setGame(cursor.getString(cursor.getColumnIndexOrThrow("game")));
                    puntuacionActual.setPlayerName(cursor.getString(cursor.getColumnIndexOrThrow("playerName")));
                    puntuacionActual.setScore(cursor.getInt(cursor.getColumnIndexOrThrow("score")));
                    puntuacionActual.setTime(cursor.getInt(cursor.getColumnIndexOrThrow("time")));
                    puntuacionActual.setTimeMode(cursor.getString(cursor.getColumnIndexOrThrow("timeMode")));

                    puntuaciones.add(puntuacionActual);
                } while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();
        return puntuaciones;
    }

    // Devuelve solo los nombres de usuario para no traer contraseñas de la base de datos
    public ArrayList<String> getUsers (){
        ArrayList<String> usuarios = new ArrayList<>();
        String usuarioActual;
        String [] columns = {"playerName"};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("users", columns, null, null, null, null, null);

        if (cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                do {
                    usuarioActual = cursor.getString(cursor.getColumnIndexOrThrow("playerName"));

                    usuarios.add(usuarioActual);
                } while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();
        return usuarios;
    }

    public boolean checkLogIn (String user, String password){
        boolean verified = false;
        String contrasena = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {"password"}, selection = {user};

        Cursor cursor = db.query("users", columns, "playerName=?", selection, null, null, null);

        if (cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                contrasena = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            }
        }

        verified = contrasena.equals(password);

        cursor.close();
        db.close();
        return verified;
    }

    public void addUser(String name, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String[] columns = {"playerName"}, selection = {name};

        Cursor cursor = db.query("users", columns, "playerName=?", selection, null, null, null);

        if (cursor.getCount() != 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_usuario_existente, null);
            builder.setView(dialogView);

            builder.setTitle("Advertencia");

            builder.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            cv.put("playerName", name);
            cv.put("password", password);
            db.insert("users", null, cv);
        }
        db.close();
    }
}
