package com.example.entregafinalandroid;

import android.app.Activity;

public class Timer implements Runnable{
    private int seconds, maxTime;
    private final String timeMode, game;
    private boolean isActive;
    private Activity activity;

    public Timer (String timeMode, String game, Activity activity){
        seconds = 0;
        maxTime = 0;
        this.timeMode = timeMode;
        isActive = true;
        this.game = game;
        this.activity = activity;
    }

    public void stop (){
        isActive = false;
    }

    @Override
    public void run() {
        if (timeMode.equals("temporizador")){
            int time = maxTime;

            while (time > 0 && isActive){
                time--;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                updateTime(time);
            }

            if (isActive){
                activity.runOnUiThread(() -> {
                    if (game.equals("2048")){
                        Game2048Activity.gameOver(activity);
                    } else if (game.equals("Giravoltorb")) {
                        GameGiravoltorbActivity.gameOver(activity);
                    }
                });
            }
        } else {
            while (isActive){
                seconds++;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                updateTime();
            }
        }
    }

    private void updateTime (){
        activity.runOnUiThread(() -> {
            if (game.equals("2048")){
                Game2048Activity.updateTimer(seconds/60 + ":" + seconds%60);
            } else if (game.equals("Giravoltorb")) {
                GameGiravoltorbActivity.updateTimer(seconds/60 + ":" + seconds%60);
            }
        });
    }

    private void updateTime (int time){
        activity.runOnUiThread(() -> {
            if (game.equals("2048")){
                Game2048Activity.updateTimer(time/60 + ":" + time%60);
            } else if (game.equals("Giravoltorb")) {
                GameGiravoltorbActivity.updateTimer(time/60 + ":" + time%60);
            }
        });
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public String getTimeMode() {
        return timeMode;
    }

    public boolean isActive() {
        return isActive;
    }
}
