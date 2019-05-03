package com.lingkarin.dev.chatapp.data.source;

import android.content.Context;

import com.lingkarin.dev.chatapp.data.models.DeliveryReceiptData;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.data.source.persistence.ChatAppLocal;
import com.lingkarin.dev.chatapp.data.source.remote.ChatFirestore;
import com.lingkarin.dev.chatapp.eventservice.XMPPEventListener;

import java.util.List;

public class Repository implements RemoteDataSource, LocalDataSource {

    private static Repository INSTANCE = null;
    private XMPPEventListener mPresenterEventListener;
    private ChatAppLocal chatAppLocal;
    private ChatFirestore chatFirestore;

    public Repository(Context context){
        this.chatAppLocal = ChatAppLocal.getInstance(context);
        this.chatFirestore = ChatFirestore.getInstance();
    }

    public static Repository getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new Repository(context);
        }
        return INSTANCE;
    }

    @Override
    public void updateMessageStatusFireStore(String id, String status, UpdateMessageStatusCallback updateMessageStatusCallback) {
        chatFirestore.updateMessageStatusFireStore(id, status, updateMessageStatusCallback);
    }

    @Override
    public void insertMessageFirestore(Message message, InsertMessageCallback insertMessageCallback){
        chatFirestore.insertMessageFirestore(message, insertMessageCallback);
    }

    @Override
    public void getMessagesFirestore(String username1, String username2, GetMessagesCallback getMessagesCallback){
        chatFirestore.getMessagesFirestore(username1, username2, getMessagesCallback);
    }

    @Override
    public void getMessageFirestore(String id, GetMessageCallback messageCallback){
        chatFirestore.getMessageFirestore(id, messageCallback);
    }

    @Override
    public void insertDeliveryReceipt(DeliveryReceiptData deliveryReceiptData) {
        chatAppLocal.insertDeliveryReceipt(deliveryReceiptData);
    }

    @Override
    public DeliveryReceiptData getDeliveryReceipt(String messageId) {
        return chatAppLocal.getDeliveryReceipt(messageId);
    }

    @Override
    public List<DeliveryReceiptData> getDeliveryReceipts(String fromJid, String toJid) {
        return chatAppLocal.getDeliveryReceipts(fromJid, toJid);
    }

    @Override
    public void updateDeliveryReceipt(String messageId, String status) {
        chatAppLocal.updateDeliveryReceipt(messageId, status);
    }

    @Override
    public void deleteDeliveryReceipt(String messageId) {
        chatAppLocal.deleteDeliveryReceipt(messageId);
    }
}
