package com.lingkarin.dev.chatapp.services.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.lingkarin.dev.chatapp.data.AppSettings;
import com.lingkarin.dev.chatapp.xmpp.XMPP;
import com.lingkarin.dev.chatapp.xmpp.XMPPChatHandler;
import com.lingkarin.dev.chatapp.xmpp.XMPPMUC;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.Objects;

public class LiveAppService extends Service {

    public static final String TAG = "MYSERVICE";
    private final LocalBinder binder = new LocalBinder();

    //    DatabaseHelper db;
    private XMPP xmppInstance;

    boolean isLoggedIn = false;
    private XMPPMUC xmppmucManager;
    private XMPPChatHandler xmppChatHandler;

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    // ***********************//

    public static Context getContext(){
        return context;
    }

    public static boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (LiveAppService.class.getName().equals(
                    service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void initListener(XMPPTCPConnection connection) {
        xmppmucManager = XMPPMUC.getInstance(connection);
        xmppChatHandler = XMPPChatHandler.getInstance(connection);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android

        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
        Log.d("MYSERVICE", "ONTASK REMOVED BROH");

    }

    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        xmppInstance = XMPP.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopService(new Intent(LiveAppService.this, FakeService.class));
            }
        }, 500);

        Log.d(TAG, "LiveService onCreate");

        if ((AppSettings.getUserName(this) != null) && (AppSettings.getPassword(this) != null)) {

                Log.d(TAG, "App not running : LiveService onCreate");
                // App is not running
                String username = AppSettings.getUserName(getApplicationContext());
                String password = AppSettings.getPassword(getApplicationContext());

                Intent connectIntent = new Intent();
                connectIntent.setAction(MyService.START_CONNECT);
                connectIntent.putExtra("username", username);
                connectIntent.putExtra("password", password);

                EventBus.getDefault().post(connectIntent);

        }

        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onLoginSuccessful(Intent intent) {
        switch (Objects.requireNonNull(intent.getAction())){
            case MyService.CONNECT_SUCCESSFUL:
                Log.d(TAG, "Listener Diinisiasi");
                initListener(XMPP.getInstance().getConnection(context));
                break;
            case MyService.CONNECT_FAIL:
                Toast.makeText(getApplicationContext(), "Login Gagal", Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MYSERVICE", "Service Destroyed");

        EventBus.getDefault().unregister(this);

        if (xmppmucManager != null && xmppChatHandler != null) {
            xmppmucManager.removeListener();
            xmppChatHandler.removeListener();
            Log.d(TAG, "Listener Dihapus");
        }
    }


    public IBinder onBind(Intent paramIntent) {
        return (IBinder) this.binder;
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public LiveAppService getService() {
            return LiveAppService.this;
        }
    }



//    private void onLoggedIn() {
//        XMPPTCPConnection connection = XMPP.getInstance().getConnection(this);
//        connection.addAsyncStanzaListener(new MessageHandler(this), null);
//    }


//        holdConnectionHandler.sendEmptyMessageDelayed(0, 3000);

//        registerReceiver(new BroadcastReceiver() {
//            public void onReceive(Context paramAnonymousContext,
//                                  Intent paramAnonymousIntent) {
//                LiveAppService.this.onLoggedIn();
//            }
//        }, new IntentFilter("liveapp.loggedin"));


//            new Thread(() -> {
//
//                xmppInstance.close();
//
//                xmppInstance = XMPP.getInstance();
//                xmppInstance.setAccount(username, password);
//
//                AndroidUsingLinkProperties.setup(getApplicationContext());
//                XMPPTCPConnection mConnection = xmppInstance.getConnection(getApplicationContext());
//
//                if (!mConnection.isConnected()){
//                    try {
//                        mConnection.connect();
//                    } catch (SmackException | IOException | InterruptedException | XMPPException e) {
//                        e.printStackTrace();
//                        Log.d(TAG, "Error auth : " + e.toString());
//                    }
//                }
//
//                if (!mConnection.isAuthenticated()) {
//                    try {
//                        mConnection.login();
//
//                        if (mConnection.isAuthenticated()) {
//                            Log.d(TAG, "login Authenticated");
//                        } else {
//                            Log.d(TAG, "login not authenticated");
//                        }
//
//                    } catch (final Exception e) {
//                        Log.d(TAG, "ERROR Auth \t " + e);
//                    }
//                }
//            }).start();




//    @SuppressLint("HandlerLeak")
//    Handler holdConnectionHandler = new Handler() {
//
//        public void handleMessage(android.os.Message msg) {
//            Log.i(TAG, "Service Handler Running");
//
//            boolean isRunning = Utils.isAppRunning(getApplication());
//            if (isRunning){
//                Log.d(TAG, "APP IS RUNNING");
//            } else {
//                Log.d(TAG, "APP IS NOT RUNNING");
//            }
//
//            XMPPTCPConnection connection = XMPP.getInstance().getConnection(getApplication());
//            if (connection == null || !connection.isConnected()) {
//                Log.i(TAG, "Service Handler Running, inside if in case of no connection");
//                final String user = AppSettings.getUserName(LiveAppService.this);
//                Log.i(TAG, "Service Handler Running, user: " + user);
//                final String password = AppSettings
//                        .getPassword(LiveAppService.this);
//                Log.i(TAG, "Service Handler Running, password: " + password);
//                final String username = AppSettings.getUserName(LiveAppService.this);
//                Log.i(TAG, "Service Handler Running, username: " + username);
//
//                if (connection != null) {
//                    if (connection.isAuthenticated()) {
//                        Log.i(TAG, "inside service handler, already authenticated");
//                        if (!isLoggedIn){
//                            initListener(XMPP.getInstance().connection);
//                            isLoggedIn = true;
//                        }
//                    } else {
//                        Log.i(TAG, "inside service handler, not authenticated, will try toJid login");
//
////                        Observable<XMPP> tempObservable = Observable.fromCallable(() -> {
////                            xmppInstance = XMPP.getInstance();
////
////                            return xmppInstance;
////                        }).subscribeOn(Schedulers.io());
////
////                        Disposable disposable = tempObservable.subscribe(xmpp -> {
////                        });
//
//                        new Thread(() -> {
//                            xmppInstance.close();
//
//                            xmppInstance = XMPP.getInstance();
//                            xmppInstance.setAccount(username, password);
//
//                            AndroidUsingLinkProperties.setup(getApplicationContext());
//                            XMPPTCPConnection mConnection = xmppInstance.getConnection(getApplicationContext());
//
//
//                            if (mConnection != null){
//                                if (!mConnection.isConnected()){
//                                    try {
//                                        mConnection.connect();
//                                    } catch (SmackException | IOException | InterruptedException | XMPPException e) {
//                                        e.printStackTrace();
//                                        Log.d(TAG, "Error auth : " + e.toString());
//                                    }
//                                }
//
//                                if (!mConnection.isAuthenticated()) {
//                                    try {
//                                        mConnection.login();
//
//                                        if (mConnection.isAuthenticated()) {
//                                            Log.d(TAG, "login Authenticated");
//
//                                            if (!isLoggedIn){
//                                                initListener(XMPP.getInstance().connection);
//                                                isLoggedIn = true;
//                                            }
//
//                                        } else {
//                                            Log.d(TAG, "login not authenticated");
//                                        }
//
//                                    } catch (final Exception e) {
//                                        Log.d(TAG, "ERROR Auth \t " + e);
//                                    }
//                                }
//                            }
//
//                        }).start();
//
//                    }
//                } else {
//                    Log.i(TAG, "inside service handler, connection is null, trying toJid login");
//                    // XMPP.getInstance().login(user, pass);
//                }
//            } else {
//                Log.i(TAG, "Service Handler Running, connection already found");
//
//                if (!connection.isAuthenticated()){
//                    initListener(XMPP.getInstance().connection);
//                    Log.d(TAG, "AUTHENTIC");
//                    isLoggedIn = true;
//                } else {
//                    if (!isLoggedIn){
//                        initListener(XMPP.getInstance().connection);
//                        isLoggedIn = true;
//                    }
//                    Log.d(TAG, "NOT AUTHENTIC");
//                }
//            }
//            holdConnectionHandler.sendEmptyMessageDelayed(0, 1000);
//        }
//
//    };

}