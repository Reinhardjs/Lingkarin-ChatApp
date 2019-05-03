package com.lingkarin.dev.chatapp.data.source;

import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.eventservice.XMPPEventListener;
import com.lingkarin.dev.chatapp.eventservice.XMPPEventService;

import java.util.List;

/**
 *  Merupakan interface yang digunakan untuk menghandle
 *  kegiatan event serverXMPP->Android dan juga Android->serverXMPP.
 *  Gabungan dari interface XMPP EventListener dan XMPP EventService
 */
public interface RemoteDataSource {
    interface GetMessageCallback {
        void onGetMessageSuccess(Message message);
    }

    interface GetMessagesCallback {
        void onGetMessagesSuccess(List<Message> messageList);
    }

    interface UpdateMessageStatusCallback {
        void onUpdateMessageStatusSuccess();
    }

    interface InsertMessageCallback {
        void onInsertMessageSuccess();
        void onIdGenerated(String id);
    }

    void updateMessageStatusFireStore(String id, String status, UpdateMessageStatusCallback updateMessageStatusCallback);

    void insertMessageFirestore(Message message, InsertMessageCallback callback);

    void getMessageFirestore(String id, GetMessageCallback callback);

    void getMessagesFirestore(String username1, String username2, GetMessagesCallback callback);
}
