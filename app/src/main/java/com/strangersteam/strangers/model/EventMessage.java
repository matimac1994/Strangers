package com.strangersteam.strangers.model;


public class EventMessage {
    private StrangerUser user;
    private String content;

    public void setUser(StrangerUser user) {
        this.user = user;
    }

    public StrangerUser getUser() {
        return user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
