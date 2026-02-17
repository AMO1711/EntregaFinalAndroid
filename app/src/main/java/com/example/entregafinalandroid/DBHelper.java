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

    public void addScore (String game, String name, String score, String time, String timeMode){
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

    public ArrayList<Score> getScores2048 (){
        ArrayList<Score> puntuaciones2048 = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Score puntuacionActual;
        String [] columns = {"_id", "playerName", "score", "time", "timeMode"};
        String [] selection = {"2048"};

        Cursor cursor = db.query("scores", columns, "game=?", selection, null, null, null);

        if (cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                do {
                    puntuacionActual = new Score();
                    puntuacionActual.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    puntuacionActual.setGame("2048");
                    puntuacionActual.setPlayerName(cursor.getString(cursor.getColumnIndexOrThrow("playerName")));
                    puntuacionActual.setScore(cursor.getString(cursor.getColumnIndexOrThrow("score")));
                    puntuacionActual.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
                    puntuacionActual.setTimeMode(cursor.getString(cursor.getColumnIndexOrThrow("timeMode")));

                    puntuaciones2048.add(puntuacionActual);
                } while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();
        return puntuaciones2048;
    }

    public ArrayList<Score> getScoresGiravoltorb (){
        ArrayList<Score> puntuacionesGiravoltorb = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Score puntuacionActual;
        String [] columns = {"_id", "playerName", "score", "time", "timeMode"};
        String [] selection = {"giravoltorb"};

        Cursor cursor = db.query("scores", columns, "game=?", selection, null, null, null);

        if (cursor.getCount() != 0){
            if (cursor.moveToFirst()){
                do {
                    puntuacionActual = new Score();
                    puntuacionActual.setId(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    puntuacionActual.setGame("giravoltorb");
                    puntuacionActual.setPlayerName(cursor.getString(cursor.getColumnIndexOrThrow("playerName")));
                    puntuacionActual.setScore(cursor.getString(cursor.getColumnIndexOrThrow("score")));
                    puntuacionActual.setTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
                    puntuacionActual.setTimeMode(cursor.getString(cursor.getColumnIndexOrThrow("timeMode")));

                    puntuacionesGiravoltorb.add(puntuacionActual);
                } while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();
        return puntuacionesGiravoltorb;
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
