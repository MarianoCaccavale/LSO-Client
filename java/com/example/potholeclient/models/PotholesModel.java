package com.example.potholeclient.models;

import androidx.annotation.NonNull;

public class PotholesModel {

    private final String nickname;
    private final Double latitude;
    private final Double longitude;


    public PotholesModel(String nickname, Double latitude, Double longitude) {
        this.nickname = nickname;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getNickname() {
        return nickname;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "Nickname: " + nickname +
                "\nLatitude=" + latitude +
                " -  Longitude=" + longitude;
    }
}
