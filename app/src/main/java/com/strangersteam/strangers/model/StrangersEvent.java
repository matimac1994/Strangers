package com.strangersteam.strangers.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.GregorianCalendar;
import java.util.List;

public class StrangersEvent {
    private Long id;
    private EventType type;
    private LatLngHolder position;
    private String title;
    private String details;
    private GregorianCalendar date;
    private List<StrangerUser> attenders;
    private List<EventMessage> messages;
    private String where;
    private StrangerUser owner;

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

    public LatLngHolder getPosition() {
        return position;
    }

    public void setPosition(LatLngHolder position) {
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

    public List<StrangerUser> getAttenders() {
        return attenders;
    }

    public void setAttenders(List<StrangerUser> attenders) {
        this.attenders = attenders;
    }

    public List<EventMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<EventMessage> messages) {
        this.messages = messages;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getWhere() {
        return where;
    }

    public void setOwner(StrangerUser owner) {
        this.owner = owner;
    }

    public StrangerUser getOwner() {
        return owner;
    }
}
