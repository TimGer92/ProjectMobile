package com.example.geritstimmymobile.model;

public class Player {
    private String playerId;
    private String firstname;
    private String lastname;
    private String birthdate;
    private String address;

    public Player() {

    }

    public Player (String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String memberId) {
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
