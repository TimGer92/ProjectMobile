package com.example.geritstimmymobile.model;

public class Player {
    private String playerId;
    private String firstname;
    private String lastname;
    private String gender;
    private String address;

    public Player() {

    }

    public Player (String firstname, String lastname, String gender) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
