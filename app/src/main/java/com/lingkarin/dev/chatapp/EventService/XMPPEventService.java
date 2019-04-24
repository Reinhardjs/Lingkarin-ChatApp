package com.lingkarin.dev.chatapp.EventService;

import java.net.URISyntaxException;

/**
 *  Ini adalah interface event service,
 *  digunakan sebagai interface untuk kejadian dari Android ke server XMPP
 *
 */
public interface XMPPEventService {

    void setEventListener(XMPPEventListener listener);

    void connect(String username) throws URISyntaxException;

    void disconnect();

//    Flowable<ChatMessage> sendMessage(ChatMessage chatMessage);

    void onTyping();

    void onStopTyping();

}
