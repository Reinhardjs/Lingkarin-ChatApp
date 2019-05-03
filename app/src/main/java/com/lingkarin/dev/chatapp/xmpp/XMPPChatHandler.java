package com.lingkarin.dev.chatapp.xmpp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.constants.Config;
import com.lingkarin.dev.chatapp.data.AppSettings;
import com.lingkarin.dev.chatapp.data.models.DeliveryReceiptData;
import com.lingkarin.dev.chatapp.data.source.Repository;
import com.lingkarin.dev.chatapp.mvp.chat.ChatActivity;
import com.lingkarin.dev.chatapp.services.service.LiveAppService;
import com.lingkarin.dev.chatapp.services.service.MyService;
import com.lingkarin.dev.chatapp.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

public class XMPPChatHandler implements IncomingChatMessageListener, OutgoingChatMessageListener,
        ReceiptReceivedListener, ChatStateListener {

    private static final String TAG = "MYSERVICE";

    private static XMPPChatHandler mXMPPChatHandler;
    private final Context context;
    private AbstractXMPPConnection mConnection;
    private ChatManager mChatManager;
    private DeliveryReceiptManager mDeliveryReceiptManager;
    private ChatStateManager mChatStateManager;

    public static final String DELIVER_REQUEST = "DELIVER-REQUEST";
    public static final String RECEIPT_REQUEST = "RECEIPT-REQUEST";
    public static final String DELIVER_RESPONSE = "DELIVER-RESPONSE";
    public static final String RECEIPT_RESPONSE = "RECEIPT-RESPONSE";

    @NonNull
    public static XMPPChatHandler getInstance(@NonNull AbstractXMPPConnection connection) {
        if (mXMPPChatHandler == null) {
            mXMPPChatHandler = new XMPPChatHandler(connection);
        }

        return mXMPPChatHandler;
    }

    private XMPPChatHandler(AbstractXMPPConnection connection) {
        mConnection = connection;
        context = LiveAppService.getContext();
        init();
    }

    @Override
    public void newIncomingMessage(EntityBareJid messageFrom, Message message, Chat chat) {
        ///ADDED
        Log.d(TAG,"message.getBody() :"+message.getBody());
        Log.d(TAG,"message.getFromJid() :"+message.getFrom());

        String from = message.getFrom().toString();

        for (ExtensionElement extension : message.getExtensions()) {

            if (extension instanceof ChatStateExtension) {

                String typing = extension.getElementName();
                Intent intent = new Intent();

                Log.d(TAG,"message.Extensio() :"+typing);

                if (typing.equals("composing")) {
                    intent.setAction(MyService.RECEIVE_ON_START_TYPING);
                } else {
                    intent.setAction(MyService.RECEIVE_ON_STOP_TYPING);
                }

                EventBus.getDefault().post(intent);
            } else if (extension instanceof DeliveryReceipt){
                // Kalau tidak dihentikan setelah ini, nanti bakalan nampilkan pesan dari "received", "received" itu dari receipt response
                return;
            }
        }

        if (!message.getBody().isEmpty()){



            if(DeliveryReceiptManager.hasDeliveryReceiptRequest(message)) {
                String messageId = message.getBody(); // message.getBody() itu adalah id message nya
                Message received = new Message();
                received.setBody(messageId);

                String myUsername = message.getTo().getLocalpartOrNull().asUnescapedString();
                String chatTo = message.getFrom().getLocalpartOrNull().asUnescapedString();

                if (Utils.isActivityForeground(context, ChatActivity.class.getName()) && Utils.isInChatRoom(myUsername, chatTo)){
                    received.setStanzaId(RECEIPT_RESPONSE);
                    Repository.getInstance(ChatApplication.getApplicationComponent().getContext())
                            .updateMessageStatusFireStore(messageId, com.lingkarin.dev.chatapp.data.models.Message.STATUS_RECEIVED, () -> {
                                // onUpdateMessageStatusSuccess
                            });
                } else {
                    received.setStanzaId(DELIVER_RESPONSE);
                    Repository.getInstance(ChatApplication.getApplicationComponent().getContext())
                            .updateMessageStatusFireStore(messageId, com.lingkarin.dev.chatapp.data.models.Message.STATUS_DELIVERED, () -> {
                                // onUpdateMessageStatusSuccess
                            });

                }


                received.addExtension(new DeliveryReceipt(message.getStanzaId()));
                received.setTo(message.getFrom());
                try {
                    received.setFrom(JidCreate.from(AppSettings.getUserName(context) + "@"+ Config.XMPP_DOMAIN));
                    XMPP.getInstance().connection.sendStanza(received);
                } catch(Exception ex) {
                    Log.d(TAG, "RECEIPT ERRRORRR : " + ex.toString());
                }

                return;
                // Kalau ga di hentikan, nanti bakalan nampilkan pesan dari receipt request yang dikirim
            }



            String contactJid="";
            if ( from.contains("/"))
            {
                contactJid = from.split("/")[0];
                Log.d(TAG,"The real jid is :" +contactJid);
                Log.d(TAG,"The message is fromJid :" +from);
            }else
            {
                contactJid=from;
            }

            Log.d(TAG,"Received message fromJid :"+contactJid+"");

            boolean isAppRunning = Utils.isAppRunning(context);
            if (isAppRunning){

                //Bundle up the intent and send the broadcast.
                Intent intent = new Intent();
                intent.setAction(MyService.NEW_MESSAGE);
                intent.putExtra(MyService.BUNDLE_FROM_JID,contactJid);
                intent.putExtra(MyService.BUNDLE_MESSAGE_BODY,message.getBody());

                EventBus.getDefault().post(intent);
            } else {

                String toUsername = message.getFrom().asBareJid().getLocalpartOrNull().asUnescapedString();

                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatToUsername", toUsername);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                Log.d(TAG, "CREATING NOTIF FROM : " + toUsername);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "A")
                        .setContentTitle(message.getFrom().toString())
                        .setContentText(message.getBody())
                        .setSmallIcon(R.drawable.bubble_circle)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);


                builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(111, builder.build());
            }
        }


    }

    @Override
    public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
        Log.d(TAG, "ChatMessageListener(newOutgoingMessage): toJid:" + to
                + ", message:" + message.toString());
    }

    @Override
    public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
        String body = ((Message) receipt).getBody();

        Log.d(TAG, "ReceiptReceivedListener(newOutgoingMessage): fromJid:" + fromJid
                + ", toJid:" + toJid + ", receiptId:" + receiptId + ", stanza:" + receipt.toString());
        Log.d(TAG, "body : " + body);
        Log.d(TAG, "FROM : " + fromJid);
        Log.d(TAG, "toJid : " + toJid);
        Log.d(TAG, "deliver request id : " + receiptId);
        Log.d(TAG, "deliver response id : " + receipt.getStanzaId());

        if (receipt.getStanzaId().equals(DELIVER_RESPONSE)){
            DeliveryReceiptData deliveryReceiptData =
                    new DeliveryReceiptData(body, toJid.getLocalpartOrNull().asUnescapedString(),
                            fromJid.getLocalpartOrNull().asUnescapedString(),
                            com.lingkarin.dev.chatapp.data.models.Message.STATUS_DELIVERED);

            if (Utils.isActivityForeground(context, ChatActivity.class.getName())){
                Intent intent = new Intent();
                intent.putExtra(DeliveryReceiptData.class.getSimpleName(), deliveryReceiptData);
                intent.setAction(MyService.STATUS_DELIVERED);
                EventBus.getDefault().post(intent);
            }

        } else if (receipt.getStanzaId().equals(RECEIPT_RESPONSE)){
            DeliveryReceiptData deliveryReceiptData =
                    new DeliveryReceiptData(body, toJid.getLocalpartOrNull().asUnescapedString(),
                            fromJid.getLocalpartOrNull().asUnescapedString(),
                            com.lingkarin.dev.chatapp.data.models.Message.STATUS_RECEIVED);

            if (Utils.isActivityForeground(context, ChatActivity.class.getName())){
                Intent intent = new Intent();
                intent.putExtra(DeliveryReceiptData.class.getSimpleName(), deliveryReceiptData);
                intent.setAction(MyService.STATUS_RECEIVED);
                EventBus.getDefault().post(intent);


            }
        }
    }

    @Override
    public void stateChanged(Chat chat, ChatState state, Message message) {
        Log.d(TAG, "ChatStateListener(stateChanged): state:" + state.toString()
                + ", message:" + message.toString());
    }

    private void init() {
        mChatManager = ChatManager.getInstanceFor(mConnection);
        mChatStateManager = ChatStateManager.getInstance(mConnection);
        mDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(XMPP.getInstance().connection);
//        mDeliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
//        mDeliveryReceiptManager.autoAddDeliveryReceiptRequests();
        setListener();
    }

    private void setListener() {
        mChatManager.addIncomingListener(this);
        mChatManager.addOutgoingListener(this);
        mDeliveryReceiptManager.addReceiptReceivedListener(this);
        mChatStateManager.addChatStateListener(this);
    }

    public void removeListener() {
        mChatManager.removeIncomingListener(this);
        mChatManager.removeOutgoingListener(this);
        mDeliveryReceiptManager.removeReceiptReceivedListener(this);
        mChatStateManager.removeChatStateListener(this);
    }
}
