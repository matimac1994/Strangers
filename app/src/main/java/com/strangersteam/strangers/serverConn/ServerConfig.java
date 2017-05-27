package com.strangersteam.strangers.serverConn;

import com.google.android.gms.maps.model.LatLngBounds;

public final class ServerConfig {
    public static final String SERVER_IP = "http://164.132.57.18:9997/";
    //public static final String SERVER_IP = "http://10.0.3.2:9997/";
    public static final String LOGIN = SERVER_IP + "user/session/login";
    public static final String GET_MARKERS = SERVER_IP + "markers";

    public static String markersOnMap(LatLngBounds mapBound) {
        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append(ServerConfig.GET_MARKERS);
        urlStringBuilder.append("?northeast_lat=");
        urlStringBuilder.append(mapBound.northeast.latitude);
        urlStringBuilder.append("&northeast_lng=");
        urlStringBuilder.append(mapBound.northeast.longitude);
        urlStringBuilder.append("&southwest_lat=");
        urlStringBuilder.append(mapBound.southwest.latitude);
        urlStringBuilder.append("&southwest_lng=");
        urlStringBuilder.append(mapBound.southwest.longitude);
        return urlStringBuilder.toString();
    }
}
