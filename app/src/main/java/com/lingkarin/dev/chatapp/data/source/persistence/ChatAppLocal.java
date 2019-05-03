package com.lingkarin.dev.chatapp.data.source.persistence;

import android.content.Context;

import com.lingkarin.dev.chatapp.data.models.DeliveryReceiptData;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.data.source.LocalDataSource;

import java.util.List;
import io.reactivex.disposables.CompositeDisposable;

public class ChatAppLocal implements LocalDataSource {

    private ChatAppDatabase mDatabase;
    private static ChatAppLocal INSTANCE;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public ChatAppLocal(Context context){
        mDatabase = ChatAppDatabase.getInstance(context);
    }

    public static ChatAppLocal getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new ChatAppLocal(context);
        }

        return INSTANCE;
    }

    public void insertMessage(Message message){
        mDatabase.messageDao().insertMessage(message);
    }

    public List<Message> getMessages(){
        return mDatabase.messageDao().getMessages();
    }

    public Message getMessage(String messageId){
        return mDatabase.messageDao().getMessage(messageId);
    }

    @Override
    public void insertDeliveryReceipt(DeliveryReceiptData deliveryReceiptData) {
        mDatabase.deliveryReceiptDao().insertDeliveryReceipt(deliveryReceiptData);
    }

    @Override
    public DeliveryReceiptData getDeliveryReceipt(String messageId) {
        return mDatabase.deliveryReceiptDao().getDeliveryReceipt(messageId);
    }

    @Override
    public List<DeliveryReceiptData> getDeliveryReceipts(String fromJid, String toJid) {
        return mDatabase.deliveryReceiptDao().getDeliveryReceipts(fromJid, toJid);
    }

    @Override
    public void updateDeliveryReceipt(String messageId, String status) {
        mDatabase.deliveryReceiptDao().updateDeliveryReceipt(messageId, status);
    }

    @Override
    public void deleteDeliveryReceipt(String messageId) {
        mDatabase.deliveryReceiptDao().deleteDeliveryReceipt(messageId);
    }
}
