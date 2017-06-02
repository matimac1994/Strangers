package com.strangersteam.strangers.model;


import java.util.GregorianCalendar;

public class StrangerEventMessage {
    private long id;
    private StrangerUser user;
    private String content;
    private GregorianCalendar date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public StrangerUser getUser() {
        return user;
    }

    public void setUser(StrangerUser user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }
}
