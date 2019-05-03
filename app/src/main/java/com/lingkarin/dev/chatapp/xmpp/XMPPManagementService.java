package com.lingkarin.dev.chatapp.xmpp;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class XMPPManagementService extends IntentService {

    private boolean mActive;
    private Thread mThread;
    private Handler mTHandler;
    private XMPPConnectionManager mXMPPConnectionManager;

    public XMPPManagementService(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel toJid the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            start();
            return;
        }

        String action = intent.getAction();

        if (XMPPServiceCommand.ACTION_CONNECT.equals(action)) {
            start();
        } else if (XMPPServiceCommand.ACTION_DISCONNECT.equals(action)) {
            stop();
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start();
        return Service.START_STICKY;
        //RETURNING START_STICKY CAUSES OUR CODE TO STICK AROUND WHEN THE APP ACTIVITY HAS DIED.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
    }

    private void start(){
        if(!mActive)
        {
            mActive = true;
            if( mThread ==null || !mThread.isAlive())
            {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Looper.prepare();
                        mTHandler = new Handler();

                        handleConnect();

                        //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                        Looper.loop();

                    }
                });
                mThread.start();
            }


        }
    }

    public void stop()
    {
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
//                if( mConnection != null)
//                {
//                    mConnection.disconnect();
//                }

                handleDisconnect();
            }
        });

    }

    private void handleConnect() {
        try {
            mXMPPConnectionManager.connect();
        } catch (Exception e) {
            Log.e("XAMPP-ERROR", "handleConnect(failure)", e);
        }
    }

    private void handleDisconnect() {
        try {
            mXMPPConnectionManager.disconnect();
        } catch (Exception e) {
            Log.e("XAMPP-ERROR", "handleConnect(failure)", e);
        }

        stopSelf();
    }
}
