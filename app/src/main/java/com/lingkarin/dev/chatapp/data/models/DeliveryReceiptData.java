package com.lingkarin.dev.chatapp.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "DeliveryReceipts")
public class DeliveryReceiptData implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "messageId")
    public String messageId;

    @ColumnInfo(name = "fromJid")
    public String fromJid;

    @ColumnInfo(name = "toJid")
    public String toJid;

    @ColumnInfo(name = "status")
    public String status;

    public DeliveryReceiptData(String messageId, String fromJid, String toJid, String status){
        this.messageId = messageId;
        this.fromJid = fromJid;
        this.toJid = toJid;
        this.status = status;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String message_id) {
        this.messageId = message_id;
    }

    public String getFromJid() {
        return fromJid;
    }

    public void setFromJid(String fromJid) {
        this.fromJid = fromJid;
    }

    public String getToJid() {
        return toJid;
    }

    public void setToJid(String toJid) {
        this.toJid = toJid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
