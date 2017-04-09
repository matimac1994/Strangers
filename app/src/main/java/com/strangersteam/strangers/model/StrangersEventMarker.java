package com.strangersteam.strangers.model;


import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.GregorianCalendar;

public class StrangersEventMarker {
    private Long id;
    private EventType type;
    private LatLng position;
    private String title;
    private String details;
    private GregorianCalendar date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }
}
