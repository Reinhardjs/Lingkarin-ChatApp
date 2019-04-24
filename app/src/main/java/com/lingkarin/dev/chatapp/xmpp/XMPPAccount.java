package com.lingkarin.dev.chatapp.xmpp;

import android.support.annotation.NonNull;

import com.lingkarin.dev.chatapp.constants.Config;
import com.google.gson.Gson;

public class XMPPAccount {

    private String username;
    private String password;
    private String domain;
    private String host;
    private int port;
    private String resource;

    public XMPPAccount(@NonNull String memberId) {
        this.username = memberId.toLowerCase();
        this.password = memberId;
        this.domain = Config.XMPP_DOMAIN;
        this.host = Config.XMPP_DOMAIN;
        this.port = Config.XMPP_PORT;
        this.resource = Config.XMPP_RESOURCE;
    }

    public String getUsername() {
        return username;
    }

    public String getDomain() {
        return domain;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static XMPPAccount fromJson(String json) {
        return new Gson().fromJson(json, XMPPAccount.class);
    }
}