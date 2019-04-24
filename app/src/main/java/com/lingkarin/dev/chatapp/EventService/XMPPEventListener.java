package com.lingkarin.dev.chatapp.EventService;

/**
 *  Ini adalah interface listener,
 *  digunakan sebagai interface untuk kejadian dari server XMPP ke Android
 *
 */
public interface XMPPEventListener {

    void onConnect(Object... args);

    void onDisconnect(Object... args);

    void onConnectError(Object... args);

    void onConnectTimeout(Object... args);

    void onNewMessage(Object... args);

    void onUserJoined(Object... args);

    void onUserLeft(Object... args);

    void onTyping(Object... args);

    void onStopTyping(Object... args);

}
