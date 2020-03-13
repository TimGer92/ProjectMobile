package com.example.geritstimmymobile.model;

public class Game {
    private String namePlayer1;
    private String namePlayer2;
    private String lifeCount1;
    private String lifeCount2;

    public Game() {
        this.lifeCount1 = "20";
        this.lifeCount2 = "20";
        this.namePlayer1 = "Player 1";
        this.namePlayer2 = "Player 2";
    }

    public String getNamePlayer1() {
        return namePlayer1;
    }

    public void setNamePlayer1(String namePlayer1) {
        this.namePlayer1 = namePlayer1;
    }

    public String getNamePlayer2() {
        return namePlayer2;
    }

    public void setNamePlayer2(String namePlayer2) {
        this.namePlayer2 = namePlayer2;
    }

    public String getLifeCount1() {
        return lifeCount1;
    }

    public void setLifeCount1(String lifeCount1) {
        this.lifeCount1 = lifeCount1;
    }

    public String getLifeCount2() {
        return lifeCount2;
    }

    public void setLifeCount2(String lifeCount2) {
        this.lifeCount2 = lifeCount2;
    }

    @Override
    public String toString() {
        return "Game{" +
                "namePlayer1='" + namePlayer1 + '\'' +
                "namePlayer2='" + namePlayer2 + '\'' +
                "lifeCount1='" + lifeCount1 + '\'' +
                "lifeCount2='" + lifeCount2 + '\'' +
                '}';
    }
}
