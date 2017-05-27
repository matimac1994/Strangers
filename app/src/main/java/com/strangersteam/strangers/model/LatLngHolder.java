package com.strangersteam.strangers.model;

/**
 * Created by kroli on 27.05.2017.
 * Klasa używana do parsowania jsona z serwera, nie mam tam androidowego LatLng więc zrobiłem taki obiekt
 */

public class LatLngHolder {

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
