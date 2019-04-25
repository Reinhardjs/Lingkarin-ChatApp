package com.lingkarin.dev.chatapp.data;

public class Credential {

    String username, password;

    public Credential(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return this.username;
    }

}
