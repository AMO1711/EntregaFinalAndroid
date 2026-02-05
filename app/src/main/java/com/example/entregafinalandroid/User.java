package com.example.entregafinalandroid;

public class User {
    private String id, player, password;

    public User(){}

    public User (String id, String player, String password){
        this.id = id;
        this.player = player;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
