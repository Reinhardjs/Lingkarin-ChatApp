package com.lingkarin.dev.chatapp.data.source.persistence.message;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.lingkarin.dev.chatapp.data.models.Message;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM Messages")
    List<Message> getMessages();

    @Query("SELECT * FROM Messages WHERE id =:messageId")
    Message getMessage(String messageId);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertMessage(Message message);

    @Update
    void updateMessage(Message message);

    @Delete
    void deleteMessage(Message message);

}
