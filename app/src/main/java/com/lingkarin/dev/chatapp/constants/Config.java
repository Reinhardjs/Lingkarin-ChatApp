package com.lingkarin.dev.chatapp.constants;

import com.lingkarin.dev.chatapp.BuildConfig;

public class Config {

    private static final String TAG = Config.class.getSimpleName();

    private Config() {
        // This class is not publicly instantiable
    }

    public static final boolean DEBUGGABLE = BuildConfig.DEBUG;
    public static final String GROUP_ID = BuildConfig.GROUP_ID;

    public static final String XMPP_DOMAIN = BuildConfig.XMPP_DOMAIN;
    public static final int XMPP_PORT = BuildConfig.XMPP_PORT;
    public static final String XMPP_RESOURCE = BuildConfig.XMPP_RESOURCE;
}