package com.strangersteam.strangers.model;

import java.util.Calendar;

public class MyUser {
    private long id;
    private String nick;
    private Calendar birthday;
    private boolean female;
    private String photoUrl;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public boolean isFemale() {
        return female;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }
}
