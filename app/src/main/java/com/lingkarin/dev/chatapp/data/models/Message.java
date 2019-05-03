package com.lingkarin.dev.chatapp.data.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.lingkarin.dev.chatapp.data.source.persistence.DateTypeConverter;
import com.lingkarin.dev.chatapp.data.source.persistence.UserTypeConverter;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "Messages")
public class Message implements Serializable, IMessage,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "msg_id")
    public int msg_id;

    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "fromJid")
    public String fromJid;

    @ColumnInfo(name = "toJid")
    public String toJid;

    @ColumnInfo(name = "status")
    public String status = STATUS_SENT;

    @TypeConverters(DateTypeConverter.class)
    @ColumnInfo(name = "createdAt")
    public Date createdAt;

    @TypeConverters(UserTypeConverter.class)
    @ColumnInfo(name = "user")
    @SerializedName("User")
    public User user;


    public transient Image image;
    public transient Voice voice;

    public static final transient String STATUS_SENT = "STATUS-SENT";
    public static final transient String STATUS_DELIVERED = "STATUS_DELIVERED";
    public static final transient String STATUS_RECEIVED = "STATUS_RECEIVED";

    public Message(){

    }

    public Message(String id, String text, String fromJid, String toJid, String status){
        this.id = id;
        this.text = text;
        this.fromJid = fromJid;
        this.toJid = toJid;
        this.status = status;
        this.createdAt = new Date();

        User user = new User(fromJid, fromJid, "http://i.imgur.com/Qn9UesZ.png", true);
        this.user = user;
    }

    public static Message getCopyFromMessagePojo(MessagePojo message){
        Message instance = new Message();
        instance.setMsg_id(message.getMsg_id());
        instance.setId(message.getId());
        instance.setText(message.getText());
        instance.setFromJid(message.getFromJid());
        instance.setToJid(message.getToJid());
        instance.setStatus(message.getStatus());
        instance.setCreatedAt(message.getCreatedAt());
        instance.setUser(message.getUser());
        instance.setImage(message.getImage());
        instance.setVoice(message.getVoice());
        return instance;
    }

    @Ignore
    public Message(String id, User user, String text) {
        this(id, user, text, new Date());
    }

    @Ignore
    public Message(String id, User user, String text, Date createdAt) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getMessageStatus(){
        return this.status;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return this.status;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public static class Image implements Serializable {

        public String url;

        public Image(){

        }

        public Image(String url) {
            this.url = url;
        }
    }

    public static class Voice implements Serializable {

        public String url;
        public int duration;

        public Voice(){

        }

        public Voice(String url, int duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public int getDuration() {
            return duration;
        }
    }
}