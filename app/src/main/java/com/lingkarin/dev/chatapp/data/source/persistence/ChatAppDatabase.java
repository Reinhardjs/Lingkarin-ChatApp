package com.lingkarin.dev.chatapp.data.source.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.lingkarin.dev.chatapp.data.models.DeliveryReceiptData;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.data.source.persistence.deliveryreceipt.DeliveryReceiptDao;
import com.lingkarin.dev.chatapp.data.source.persistence.message.MessageDao;

@Database(entities =  {Message.class, DeliveryReceiptData.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class, UserTypeConverter.class})
public abstract class ChatAppDatabase extends RoomDatabase {

    private static volatile ChatAppDatabase INSTANCE;

    public abstract MessageDao messageDao();

    public abstract DeliveryReceiptDao deliveryReceiptDao();

    public static ChatAppDatabase getInstance(Context context){
        if (INSTANCE == null){
            synchronized (ChatAppDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ChatAppDatabase.class, "ChatApp.db")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }

        return INSTANCE;
    }

}
