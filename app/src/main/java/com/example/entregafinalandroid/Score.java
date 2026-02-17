package com.example.entregafinalandroid;

public class Score {
    private String id, game, playerName, score, time, timeMode;

    public Score (){

    }

    public Score (String id, String game, String playerName, String score, String time, String timeMode){
        this.id = id;
        this.game = game;
        this.playerName = playerName;
        this.score = score;
        this.time = time;
        this.timeMode = timeMode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeMode() {
        return timeMode;
    }

    public void setTimeMode(String timeMode) {
        this.timeMode = timeMode;
    }
}
