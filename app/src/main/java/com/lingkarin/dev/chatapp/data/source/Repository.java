package com.lingkarin.dev.chatapp.data.source;

import com.lingkarin.dev.chatapp.eventservice.XMPPEventListener;

import java.net.URISyntaxException;

public class Repository implements DataSource {

    private static Repository INSTANCE = null;
    private XMPPEventListener mPresenterEventListener;

    public Repository(){

    }

    public static Repository getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Repository();
        }
        return INSTANCE;
    }


    // XMPPEventService
    // ---------------------------------------------------------------
    @Override
    public void setEventListener(XMPPEventListener listener) {
        mPresenterEventListener = listener;
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
        if (mPresenterEventListener != null)
        mPresenterEventListener.onConnect(args);
    }

    @Override
    public void onDisconnect(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnect(args);
    }

    @Override
    public void onConnectError(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnect(args);
    }

    @Override
    public void onConnectTimeout(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnect(args);
    }

    @Override
    public void onNewMessage(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnect(args);
    }

    @Override
    public void onUserJoined(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnect(args);
    }

    @Override
    public void onUserLeft(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnect(args);
    }

    @Override
    public void onTyping(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnect(args);
    }

    @Override
    public void onStopTyping(Object... args) {
        if (mPresenterEventListener != null)
            mPresenterEventListener.onConnect(args);
    }


}
