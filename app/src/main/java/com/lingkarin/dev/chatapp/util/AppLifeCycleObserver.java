package com.lingkarin.dev.chatapp.util;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.widget.Toast;

public class AppLifeCycleObserver implements LifecycleObserver {

    private Context mContext;

    /**
     * Use this constructor to create a new AppLifeCycleObserver
     *
     * @param context
     */
    public AppLifeCycleObserver(Context context) {
        mContext = context;
    }

    /**
     * When app enters foreground
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        // EventServiceImpl.getInstance().connect(User.getUsername());
        Toast.makeText(mContext, "Enter Foreground", Toast.LENGTH_SHORT).show();
    }

    /**
     * When app enters background
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        // EventServiceImpl.getInstance().disconnect();
        Toast.makeText(mContext, "Enter Background", Toast.LENGTH_SHORT).show();
    }

}
