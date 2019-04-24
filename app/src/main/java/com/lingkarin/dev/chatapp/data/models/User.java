package com.lingkarin.dev.chatapp.data.models;

public class User {
    String username;
    String password;
    String jid;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getJid() {
        return jid;
    }
}
