package com.lingkarin.dev.chatapp.data.source.persistence.deliveryreceipt;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.lingkarin.dev.chatapp.data.models.DeliveryReceiptData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface DeliveryReceiptDao {

//    @Query("SELECT * FROM DeliveryReceipts WHERE fromJid = :fromJid AND toJid = :toJid")
//    Flowable<List<DeliveryReceiptData>> getDeliveryReceipts(String fromJid, String toJid);
//
//    @Query("SELECT * FROM DeliveryReceipts WHERE messageId = :messageId")
//    Flowable<DeliveryReceiptData> getDeliveryReceipt(String messageId);

    @Query("SELECT * FROM DeliveryReceipts WHERE fromJid = :fromJid AND toJid = :toJid")
    List<DeliveryReceiptData> getDeliveryReceipts(String fromJid, String toJid);

    @Query("SELECT * FROM DeliveryReceipts WHERE messageId = :messageId")
    DeliveryReceiptData getDeliveryReceipt(String messageId);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insertDeliveryReceipt(DeliveryReceiptData deliveryReceiptData);

//    @Update
//    void updateDeliveryReceipt(DeliveryReceiptData deliveryReceiptData);

//    @Delete
//    void deleteDeliveryReceipt(DeliveryReceiptData deliveryReceiptData);

    @Query("UPDATE DeliveryReceipts SET status = :status WHERE messageId = :messageId")
    void updateDeliveryReceipt(String messageId, String status);

    @Query("DELETE FROM DeliveryReceipts WHERE messageId = :messageId")
    void deleteDeliveryReceipt(String messageId);

}
