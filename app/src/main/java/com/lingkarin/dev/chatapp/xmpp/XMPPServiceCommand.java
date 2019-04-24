package com.lingkarin.dev.chatapp.xmpp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.annotation.NonNull;

public class XMPPServiceCommand extends ContextWrapper {

    private static final String TAG = XMPPServiceCommand.class.getSimpleName();

    public static final String ACTION_CONNECT = "connect";
    public static final String ACTION_DISCONNECT = "disconnect";

    private XMPPServiceCommand(@NonNull Context context) {
        super(context);
    }

    public static XMPPServiceCommand getInstance(@NonNull Context context) {
        return new XMPPServiceCommand(context);
    }

    public void connect() {
        Intent intent = new Intent(this, XMPPService.class);
        intent.setAction(ACTION_CONNECT);
        startService(intent);
    }

    public void disconnect() {
        Intent intent = new Intent(this, XMPPService.class);
        intent.setAction(ACTION_DISCONNECT);
        startService(intent);
    }
}