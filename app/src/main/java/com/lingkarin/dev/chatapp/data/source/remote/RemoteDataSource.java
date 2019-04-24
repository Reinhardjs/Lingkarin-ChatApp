package com.lingkarin.dev.chatapp.data.source.remote;

import com.lingkarin.dev.chatapp.eventservice.XMPPEventListener;
import com.lingkarin.dev.chatapp.eventservice.XMPPEventService;
import com.lingkarin.dev.chatapp.data.source.DataSource;

import java.net.URISyntaxException;
import java.util.EventListener;

public class RemoteDataSource implements DataSource {

    private static RemoteDataSource INSTANCE;
    private static XMPPEventService mEventService;
    private EventListener mRepoEventListener;

    public RemoteDataSource(){
        mEventService.setEventListener(this);
    }

    public RemoteDataSource getInstance(){
        if (INSTANCE == null){
            INSTANCE = new RemoteDataSource();
        }

        return INSTANCE;
    }


    // XMPPEventService
    // ---------------------------------------------------------------
    @Override
    public void setEventListener(XMPPEventListener listener) {

    }

    @Override
    public void connect(String username) throws URISyntaxException {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void onTyping() {

    }

    @Override
    public void onStopTyping() {

    }


    // XMPPEventListener
    // ---------------------------------------------------------------

    @Override
    public void onConnect(Object... args) {

    }

    @Override
    public void onDisconnect(Object... args) {

    }

    @Override
    public void onConnectError(Object... args) {

    }

    @Override
    public void onConnectTimeout(Object... args) {

    }

    @Override
    public void onNewMessage(Object... args) {

    }

    @Override
    public void onUserJoined(Object... args) {

    }

    @Override
    public void onUserLeft(Object... args) {

    }

    @Override
    public void onTyping(Object... args) {

    }

    @Override
    public void onStopTyping(Object... args) {

    }

}
