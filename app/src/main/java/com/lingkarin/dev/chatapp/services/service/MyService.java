package com.lingkarin.dev.chatapp.services.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.lingkarin.dev.chatapp.constants.Config;
import com.lingkarin.dev.chatapp.data.AppSettings;
import com.lingkarin.dev.chatapp.utils.Utils;
import com.lingkarin.dev.chatapp.xmpp.XMPP;
import com.lingkarin.dev.chatapp.xmpp.XMPPChatHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.minidns.dnsserverlookup.android21.AndroidUsingLinkProperties;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyService extends Service {

    public static final String BUNDLE_FROM_JID = "FROM_JID";
    public static final String BUNDLE_MESSAGE_BODY = "MESSAGE_BODY";
    Thread mThread;
    Handler mHandler;

    private static int count = 0;

    public static final String START_CONNECT = "START-CONNECT";
    public static final String CONNECT_SUCCESSFUL = "CONNECT_SUCCESSFUL";
    public static final String CONNECT_FAIL = "CONNECT_FAIL";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String SEND_MESSAGE = "SEND_MESSAGE";
    public static final String NEW_MESSAGE = "NEW_MESSAGE";
    public static final String SEND_ON_START_TYPING = "SEND_ON_START_TYPING";
    public static final String SEND_ON_STOP_TYPING = "SEND_ON_STOP_TYPING";
    public static final String RECEIVE_ON_START_TYPING = "RECEIVE_ON_START_TYPING";
    public static final String RECEIVE_ON_STOP_TYPING = "RECEIVE_ON_STOP_TYPING";

    public static final String STATUS_DELIVERED = "STATUS_DELIVERED";
    public static final String STATUS_RECEIVED = "STATUS_RECEIVED";

    private XMPP xmppInstance;

    public static boolean firstTimeReactiveNetwork = true;

    public Context getContext(){
        return this;
    }

    public MyService getInstance(){
        return this;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d("MYSERVICE", "ONSTART COMMAND BROH");
        return START_STICKY;
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

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.d("MYSERVICE", "ONCREATE BROH");

        Disposable internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {


                    if (!firstTimeReactiveNetwork && !Utils.isAppRunning(getApplication())){
                        if (isConnected) {
                            Utils.setInternetStatus(Utils.INTERNET_CONNECTED);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } else {
                            Utils.setInternetStatus(Utils.INTERNET_DISCONNECTED);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    }

                    firstTimeReactiveNetwork = false;


                });

        // toJid make the service become endless :
        // start a separate thread and start listening toJid your network object
        if( mThread ==null || !mThread.isAlive())
        {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Looper.prepare();
                    mHandler = new Handler();
                    //initConnection();

                    startTimer();

                    //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                    Looper.loop();

                }
            });
            mThread.start();
        }


        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode =  ThreadMode.BACKGROUND)
    public void onConnectEvent(Intent intent){

        switch (Objects.requireNonNull(intent.getAction())){
            case MyService.START_CONNECT:
                Log.d("MYSERVICE", "START CONNECT");
                connect(intent);
                break;
            case MyService.DISCONNECT:
                disconnect();
                break;
            case MyService.SEND_MESSAGE:
                Log.d("MYSERVICE", "SEND MESSAGE " + intent.getStringExtra("body"));
                sendMessage(intent);
                break;
            case MyService.SEND_ON_START_TYPING:
                Log.d("MYSERVICE", "SERVICE OM START TYPING");
                sendOnStartTyping(intent);
                break;
            case MyService.SEND_ON_STOP_TYPING:
                Log.d("MYSERVICE", "SERVICE OM STOP TYPING");
                sendOnStopTyping(intent);
                break;
        }
    }

    public void disconnect(){
        XMPP.getInstance().close();
        AppSettings.clearAll(getApplicationContext());
    }

    public void connect(Intent intent){
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String chatTo = intent.getStringExtra("chatTo");

        AndroidUsingLinkProperties.setup(getApplication());
        xmppInstance = XMPP.getInstance();
        xmppInstance.setAccount(username, password);

        XMPPTCPConnection mConnection = xmppInstance.getConnection(getApplication());
        Intent connectResponse = new Intent();

        if (!mConnection.isConnected()){
            try {
                mConnection.connect();
            } catch (SmackException | IOException | InterruptedException | XMPPException e) {
                e.printStackTrace();
                Log.d("MYSERVICE", "Error auth : " + e.toString());
            }
        }

        if (!mConnection.isAuthenticated()) {
            try {
                mConnection.login();

                if (mConnection.isAuthenticated()) {
                    Log.d("MYSERVICE", "login Authenticated");
                } else {
                    Log.d("MYSERVICE", "login not authenticated");
                }

            } catch (final Exception e) {
                Log.d("MYSERVICE", "ERROR Auth \t " + e);
            }
        }

        connectResponse.setAction(MyService.CONNECT_SUCCESSFUL);
        EventBus.getDefault().post(connectResponse);

        Log.d("MYSERVICE", "connect and login completed");



//        initListener(XMPP.getInstance().connection);

//        DeliveryReceiptManager mDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(XMPP.getInstance().connection);
//        mDeliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
//        mDeliveryReceiptManager.autoAddDeliveryReceiptRequests();
//        mDeliveryReceiptManager.addReceiptReceivedListener((fromJid, toJid, receiptId, receipt) -> {
//            Log.d("MYSERVICE", "ReceiptReceivedListener(newOutgoingMessage): fromJid:" + fromJid
//                    + ", toJid:" + toJid + ", receiptId:" + receiptId + ", stanza:" + receipt.toString());
//            Log.d("MYSERVICE", "FROM : " + fromJid);
//            Log.d("MYSERVICE", "toJid : " + toJid);
//            Log.d("MYSERVICE", "receipt id : " + receiptId);
//            Log.d("MYSERVICE", "Stanza id : " + receipt.getStanzaId());
//        });
    }


    private void sendMessage (Intent intent)
    {

        com.lingkarin.dev.chatapp.data.models.Message body = (com.lingkarin.dev.chatapp.data.models.Message) intent.getSerializableExtra("body");
        String toJid = intent.getStringExtra("toJid");

        ChatManager chatManager = ChatManager.getInstanceFor(XMPP.getInstance().connection);

        Log.d("MYSERVICE","Sending message toJid :"+ toJid);
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(toJid + "@" + Config.XMPP_DOMAIN);
            Chat chat = chatManager.chatWith(jid);

            Message message = new Message(jid, Message.Type.chat);
            message.setBody(Utils.messageToJson(body));
            chat.send(message);

            Log.d("MYSERVICE","Sending message succes :"+ jid.getLocalpart());


            Message msg = new Message(jid, Message.Type.chat);
            msg.setBody(body.getId());
            msg.setStanzaId(XMPPChatHandler.DELIVER_REQUEST);
            msg.setFrom(JidCreate.from(AppSettings.getUserName(this) + "@"+ Config.XMPP_DOMAIN));
            DeliveryReceiptRequest.addTo(msg);
            XMPP.getInstance().connection.sendStanza(msg);


        } catch (XmppStringprepException e){
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            Log.d("MYSERVICE", "not connected SEND MESSAGE ERROR " + e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d("MYSERVICE", "interrupt SEND MESSAGE ERROR " + e.toString());
            e.printStackTrace();
        }
    }

    private void sendOnStartTyping(Intent intent){

        String toJid = intent.getStringExtra("toJid");
        ChatManager chatManager = ChatManager.getInstanceFor(XMPP.getInstance().connection);

        Log.d("MYSERVICE","Sending start onTyping toJid :"+ toJid);
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(toJid + "@" + Config.XMPP_DOMAIN);
            Chat chat = chatManager.chatWith(jid);

            ChatStateExtension ext = new ChatStateExtension(
                    ChatState.composing);

            Message message = new Message(jid, Message.Type.chat);
            message.addExtension(ext);
            message.setBody("");

            AndroidUsingLinkProperties.setup(getApplicationContext());
            chat.send(message);

            Log.d("MYSERVICE","Sending message succes :"+ jid.getLocalpart());

        } catch (XmppStringprepException e){
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            Log.d("MYSERVICE", "not connected SEND MESSAGE ERROR " + e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d("MYSERVICE", "interrupt SEND MESSAGE ERROR " + e.toString());
            e.printStackTrace();
        }
    }

    private void sendOnStopTyping(Intent intent){

        String toJid = intent.getStringExtra("toJid");
        ChatManager chatManager = ChatManager.getInstanceFor(XMPP.getInstance().connection);

        Log.d("MYSERVICE","Sending stop onTyping toJid :"+ toJid);
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(toJid + "@" + Config.XMPP_DOMAIN);
            Chat chat = chatManager.chatWith(jid);

            ChatStateExtension ext = new ChatStateExtension(
                    ChatState.paused);

            Message message = new Message(jid, Message.Type.chat);
            message.addExtension(ext);
            message.setBody("");

            AndroidUsingLinkProperties.setup(getApplicationContext());
            chat.send(message);

            Log.d("MYSERVICE","Sending message succes :"+ jid.getLocalpart());

        } catch (XmppStringprepException e){
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            Log.d("MYSERVICE", "not connected SEND MESSAGE ERROR " + e.toString());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d("MYSERVICE", "interrupt SEND MESSAGE ERROR " + e.toString());
            e.printStackTrace();
        }
    }





    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, toJid wake up every 1 second
        timer.schedule(timerTask, 1000, 2000); //
    }

    /**
     * it sets the timer toJid print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                // Log.i("in timer", "in timer ++++  "+ (counter++));
                Log.d("MYSERVICE", "KEEP RUNNING :) " + count);
                count++;

            }
        };
    }

    public static boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(
                    service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}