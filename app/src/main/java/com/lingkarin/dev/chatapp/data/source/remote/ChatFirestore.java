package com.lingkarin.dev.chatapp.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.data.models.MessagePojo;
import com.lingkarin.dev.chatapp.data.source.RemoteDataSource;
import com.lingkarin.dev.chatapp.data.source.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatFirestore implements RemoteDataSource {

    private static ChatFirestore INSTANCE;
    private FirebaseFirestore db;
    public static final String TAG = "MYSERVICE;";

    public ChatFirestore(){

        FirebaseFirestore.setLoggingEnabled(true);
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

    }

    public static ChatFirestore getInstance(){
        if (INSTANCE == null){
            INSTANCE = new ChatFirestore();
        }
        return INSTANCE;
    }

    @Override
    public void updateMessageStatusFireStore(String id, String status, UpdateMessageStatusCallback updateMessageStatusCallback) {
        db.collection("messages").document(id)
                .update("status", status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateMessageStatusCallback.onUpdateMessageStatusSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void insertMessageFirestore(Message message, InsertMessageCallback insertMessageCallback){
        MessagePojo messagePojo = MessagePojo.getCopyFromMessage(message);
        DocumentReference docRef = db.collection("messages").document();
        //messagePojo.getId() + "-"

        String id = docRef.getId();
        messagePojo.setId(id);

        insertMessageCallback.onIdGenerated(id);

        docRef.set(messagePojo)
                .addOnSuccessListener(aVoid -> {
                    insertMessageCallback.onInsertMessageSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    @Override
    public void getMessageFirestore(String id, GetMessageCallback getMessageCallback){
        CollectionReference messagesRef = db.collection("messages");
        Query messageQuery = messagesRef.whereEqualTo("id", id);
        messageQuery
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                Message message = documentSnapshot.toObject(Message.class);
                                getMessageCallback.onGetMessageSuccess(message);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    // Query untuk message si user ke opponent
    @Override
    public void getMessagesFirestore(String username1, String username2, GetMessagesCallback getMessagesCallback){
        CollectionReference messagesRef = db.collection("messages");
        messagesRef
                .whereEqualTo("fromJid", username1)
                .whereEqualTo("toJid", username2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<MessagePojo> messageList = new ArrayList<>();

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                MessagePojo message = documentSnapshot.toObject(MessagePojo.class);
                                messageList.add(message);
                            }

                            getMessagesPart2(messageList, username1, username2, getMessagesCallback);
                        }
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    // Query untuk message opponent ke user
    private void getMessagesPart2(List<MessagePojo> messageList, String username1, String username2, GetMessagesCallback getMessagesCallback){
        Log.d("MYSERVICE", "MessAGE || " + username1 + " -> " + username2);
        CollectionReference messagesRef = db.collection("messages");
        messagesRef
                .whereEqualTo("fromJid", username2)
                .whereEqualTo("toJid", username1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                MessagePojo message = documentSnapshot.toObject(MessagePojo.class);
                                messageList.add(message);
                                Log.d("MYSERVICE", "MessAGE " + message.getText());
                            }


                            Collections.sort(messageList);

                            List<Message> messageListResult = new ArrayList<>();

                            for (MessagePojo messagePojo : messageList){
                                messageListResult.add(Message.getCopyFromMessagePojo(messagePojo));

                                Repository.getInstance(ChatApplication.getApplicationComponent().getContext())
                                        .updateMessageStatusFireStore("Fri May 03 12:33:04 GMT+07:00 2019", Message.STATUS_RECEIVED, () -> {
                                            // onUpdateMessageStatusSuccess

                                        });
                            }

                            getMessagesCallback.onGetMessagesSuccess(messageListResult);
                        }
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

}










//package com.lingkarin.dev.chatapp.data.source.remote;
//
//import android.support.annotation.NonNull;
//import android.util.Log;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.FirebaseFirestoreSettings;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.lingkarin.dev.chatapp.data.models.Message;
//import com.lingkarin.dev.chatapp.data.models.MessagePojo;
//import com.lingkarin.dev.chatapp.data.source.RemoteDataSource;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class ChatFirestore implements RemoteDataSource {
//
//    private static ChatFirestore INSTANCE;
//    private FirebaseFirestore db;
//    public static final String TAG = "MYSERVICE;";
//
//    public ChatFirestore(){
//
//        FirebaseFirestore.setLoggingEnabled(true);
//        db = FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build();
//        db.setFirestoreSettings(settings);
//
//    }
//
//    public static ChatFirestore getInstance(){
//        if (INSTANCE == null){
//            INSTANCE = new ChatFirestore();
//        }
//        return INSTANCE;
//    }
//
//    @Override
//    public void insertMessageFirestore(Message message, InsertMessageCallback insertMessageCallback){
//        MessagePojo messagePojo = MessagePojo.getCopyFromMessage(message);
//        db.collection("messages")
//                .add(messagePojo)
//                .addOnSuccessListener(documentReference -> {
//                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//                    insertMessageCallback.onInsertMessageSuccess();
//                })
//                .addOnFailureListener(e -> {
//                    Log.w(TAG, "Error adding document", e);
//                });
//    }
//
//    @Override
//    public void getMessageFirestore(String id, GetMessageCallback getMessageCallback){
//        CollectionReference messagesRef = db.collection("messages");
//        Query messageQuery = messagesRef.whereEqualTo("id", id);
//        messageQuery
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                                Message message = documentSnapshot.toObject(Message.class);
//                                getMessageCallback.onGetMessageSuccess(message);
//                            }
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//
//                });
//    }
//
//    // Query untuk message si user ke opponent
//    @Override
//    public void getMessagesFirestore(String username1, String username2, GetMessagesCallback getMessagesCallback){
//        CollectionReference messagesRef = db.collection("messages");
//        messagesRef
//                .whereEqualTo("fromJid", username1)
//                .whereEqualTo("toJid", username2)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//                            List<Message> messageList = new ArrayList<>();
//
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                                Message message = Message.getCopyFromMessagePojo(documentSnapshot.toObject(MessagePojo.class));
//                                messageList.add(message);
//                            }
//
//                            getMessagesPart2(messageList, username1, username2, getMessagesCallback);
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//
//                });
//    }
//
//    // Query untuk message opponent ke user
//    private void getMessagesPart2(List<Message> messageList, String username1, String username2, GetMessagesCallback getMessagesCallback){
//        Log.d("MYSERVICE", "MessAGE || " + username1 + " -> " + username2);
//        CollectionReference messagesRef = db.collection("messages");
//        messagesRef
//                .whereEqualTo("fromJid", username2)
//                .whereEqualTo("toJid", username1)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()){
//
//                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                                Message message = Message.getCopyFromMessagePojo(documentSnapshot.toObject(MessagePojo.class));
//                                messageList.add(message);
//                                Log.d("MYSERVICE", "MessAGE " + message.getText());
//                            }
//
//                            getMessagesCallback.onGetMessagesSuccess(messageList);
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//
//                });
//    }
//
//}