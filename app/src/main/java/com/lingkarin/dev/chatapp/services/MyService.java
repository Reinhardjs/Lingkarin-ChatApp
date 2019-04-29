package com.lingkarin.dev.chatapp.services;

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

import com.lingkarin.dev.chatapp.constants.Config;
import com.lingkarin.dev.chatapp.xmpp.XMPP;
import com.lingkarin.dev.chatapp.xmpp.XMPPMUC;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.minidns.dnsserverlookup.android21.AndroidUsingLinkProperties;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    public static final String BUNDLE_FROM_JID = "FROM_JID";
    public static final String BUNDLE_MESSAGE_BODY = "MESSAGE_BODY";
    Thread mThread;
    Handler mHandler;

    private static int count = 0;

    public static final String START_CONNECT = "START-CONNECT";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String SEND_MESSAGE = "SEND_MESSAGE";
    public static final String NEW_MESSAGE = "NEW_MESSAGE";
    public static final String SEND_ON_START_TYPING = "SEND_ON_START_TYPING";
    public static final String SEND_ON_STOP_TYPING = "SEND_ON_STOP_TYPING";
    public static final String RECEIVE_ON_START_TYPING = "RECEIVE_ON_START_TYPING";
    public static final String RECEIVE_ON_STOP_TYPING = "RECEIVE_ON_STOP_TYPING";


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

        // to make the service become endless :
        // start a separate thread and start listening to your network object

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

    private void setupMUCListener() {
        XMPPMUC xmppmucManager = XMPPMUC.getInstance(XMPP.getInstance().getConnection(getApplicationContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MYSERVICE", "Service Destroyed");
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode =  ThreadMode.BACKGROUND)
    public void onConnectEvent(Intent intent){

        Log.d("MYSERVICE", "HELLO");

        switch (Objects.requireNonNull(intent.getAction())){
            case MyService.START_CONNECT:
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

    private void setupMessageListener(){
        ChatManager.getInstanceFor(XMPP.getInstance().connection).addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid messageFrom, Message message, Chat chat) {
                ///ADDED
                Log.d("MYSERVICE","message.getBody() :"+message.getBody());
                Log.d("MYSERVICE","message.getFrom() :"+message.getFrom());

                String from = message.getFrom().toString();

                for (ExtensionElement extension : message.getExtensions()) {

                    if (extension instanceof ChatStateExtension) {

                        String typing = extension.getElementName();
                        Intent intent = new Intent();

                        Log.d("MYSERVICE","message.Extensio() :"+typing);

                        if (typing.equals("composing")) {
                            intent.setAction(MyService.RECEIVE_ON_START_TYPING);
                        } else {
                            intent.setAction(MyService.RECEIVE_ON_STOP_TYPING);
                        }

                        EventBus.getDefault().post(intent);
                    }
                }

                if (!message.getBody().isEmpty()){
                    String contactJid="";
                    if ( from.contains("/"))
                    {
                        contactJid = from.split("/")[0];
                        Log.d("MYSERVICE","The real jid is :" +contactJid);
                        Log.d("MYSERVICE","The message is from :" +from);
                    }else
                    {
                        contactJid=from;
                    }

                    Log.d("MYSERVICE","Received message from :"+contactJid+"");

                    //Bundle up the intent and send the broadcast.
                    Intent intent = new Intent();
                    intent.setAction(MyService.NEW_MESSAGE);
                    intent.putExtra(MyService.BUNDLE_FROM_JID,contactJid);
                    intent.putExtra(MyService.BUNDLE_MESSAGE_BODY,message.getBody());

                    EventBus.getDefault().post(intent);

//                    DeliveryReceiptManager deliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(XMPP.getInstance().connection);
                    if(DeliveryReceiptManager.hasDeliveryReceiptRequest(message)) {
                        Stanza received = new Message();
                        received.addExtension(new DeliveryReceipt(message.getStanzaId()));
                        received.setTo(message.getFrom());
                        try {
                            XMPP.getInstance().connection.sendStanza(received);
                        } catch(Exception ex) {
                            Log.d("MYSERVICE", "RECEIPT ERRRORRR : " + ex.toString());
                        }
                    }
                }


            }
        });
    }

    private void sendMessage (Intent intent)
    {

        String body = intent.getStringExtra("body");
        String toJid = intent.getStringExtra("toJid");


        ChatManager chatManager = ChatManager.getInstanceFor(XMPP.getInstance().connection);

        Log.d("MYSERVICE","Sending message to :"+ toJid);
        try {
            EntityBareJid jid = JidCreate.entityBareFrom(toJid + "@" + Config.XMPP_DOMAIN);
            Chat chat = chatManager.chatWith(jid);

            Message message = new Message(jid, Message.Type.chat);
            message.setBody(body);
            DeliveryReceiptRequest.addTo(message);

            chat.send(message);

            Log.d("MYSERVICE","Sending message succes :"+ jid.getLocalpart());


//            Message msg = new Message(jid, Message.Type.chat);
//            msg.setBody(body);
//            msg.setStanzaId("ini-id-1");
//            msg.setFrom(JidCreate.from("chat1@"+ Config.XMPP_DOMAIN));
//            DeliveryReceiptRequest.addTo(msg);
//            XMPP.getInstance().connection.sendStanza(msg);


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

        Log.d("MYSERVICE","Sending start onTyping to :"+ toJid);
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

        Log.d("MYSERVICE","Sending stop onTyping to :"+ toJid);
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

    public void disconnect(){
        XMPP.getInstance().close();
    }

    public void connect(Intent intent){
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String chatTo = intent.getStringExtra("chatTo");

        AndroidUsingLinkProperties.setup(getApplicationContext());

        XMPP xmppInstance = XMPP.getInstance();
        xmppInstance.setAccount(username, password);

        XMPPTCPConnection mConnection = xmppInstance.getConnection(getApplicationContext());


        try {
            mConnection.connect();
        } catch (final Exception e) {
            Log.d("MYSERVICE", ".connect() ERROR Auth \t " + e);
        }

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

        Log.d("MYSERVICE", "connect and login completed");
        setupMUCListener();
        setupMessageListener();



        DeliveryReceiptManager mDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(XMPP.getInstance().connection);
//        mDeliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
        mDeliveryReceiptManager.autoAddDeliveryReceiptRequests();
        mDeliveryReceiptManager.addReceiptReceivedListener(new ReceiptReceivedListener()
        {
            @Override
            public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
                Log.d("MYSERVICE", "ReceiptReceivedListener(newOutgoingMessage): from:" + fromJid
                        + ", to:" + toJid + ", receiptId:" + receiptId + ", stanza:" + receipt.toString());
                Log.d("MYSERVICE", "FROM : " + fromJid);
                Log.d("MYSERVICE", "to : " + toJid);
                Log.d("MYSERVICE", "receipt id : " + receiptId);
                Log.d("MYSERVICE", "Stanza id : " + receipt.getStanzaId());
            }
        });



    }




    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 2000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
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