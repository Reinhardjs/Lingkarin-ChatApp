package com.lingkarin.dev.chatapp.xmpp;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.ChatStateManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

public class XMPPChatManager implements IncomingChatMessageListener, OutgoingChatMessageListener,
        ReceiptReceivedListener, ChatStateListener {

    private static final String TAG = XMPPChatManager.class.getSimpleName();

    private static XMPPChatManager mXMPPChatManager;
    private AbstractXMPPConnection mConnection;
    private ChatManager mChatManager;
    private DeliveryReceiptManager mDeliveryReceiptManager;
    private ChatStateManager mChatStateManager;

    @NonNull
    public static XMPPChatManager getInstance(@NonNull AbstractXMPPConnection connection) {
        if (mXMPPChatManager == null) {
            mXMPPChatManager = new XMPPChatManager(connection);
        }

        return mXMPPChatManager;
    }

    private XMPPChatManager(AbstractXMPPConnection connection) {
        mConnection = connection;
        init();
    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        Log.d(TAG, "ChatMessageListener(newIncomingMessage): from:" + from
                + ", message:" + message.toString());
    }

    @Override
    public void newOutgoingMessage(EntityBareJid to, Message message, Chat chat) {
        Log.d(TAG, "ChatMessageListener(newOutgoingMessage): to:" + to
                + ", message:" + message.toString());
    }

    @Override
    public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {
        Log.d(TAG, "ReceiptReceivedListener(newOutgoingMessage): from:" + fromJid
                + ", to:" + toJid + ", receiptId:" + receiptId + ", stanza:" + receipt.toString());
    }

//    @Override
//    public void stateChanged(Chat chat, ChatState state, Message message) {
//        Log.d(TAG, "ChatStateListener(stateChanged): state:" + state.toString()
//                + ", message:" + message.toString());
//    }

    private void init() {
        mChatManager = ChatManager.getInstanceFor(mConnection);
        mDeliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(mConnection);
        mDeliveryReceiptManager.autoAddDeliveryReceiptRequests();
        mChatStateManager = ChatStateManager.getInstance(mConnection);
        setListener();
    }

    private void setListener() {
        mChatManager.addIncomingListener(this);
        mChatManager.addOutgoingListener(this);
        mDeliveryReceiptManager.addReceiptReceivedListener(this);
//        mChatStateManager.addChatStateListener(this);
    }

    public void removeListener() {
//        mChatManager.removeIncomingListener(this);
//        mChatManager.removeOutgoingListener(this);
        mDeliveryReceiptManager.removeReceiptReceivedListener(this);
//        mChatStateManager.removeChatStateListener(this);
    }

    @Override
    public void stateChanged(org.jivesoftware.smack.chat.Chat chat, ChatState state, Message message) {

    }

    @Override
    public void processMessage(org.jivesoftware.smack.chat.Chat chat, Message message) {

    }
}
