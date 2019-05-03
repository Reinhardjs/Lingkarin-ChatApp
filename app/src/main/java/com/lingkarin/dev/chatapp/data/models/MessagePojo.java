package com.lingkarin.dev.chatapp.data.models;

import java.io.Serializable;
import java.util.Date;

import static com.lingkarin.dev.chatapp.data.models.Message.STATUS_SENT;

public class MessagePojo implements Serializable, Comparable<MessagePojo> {

    private int msg_id;
    private String id;
    private String text;
    private String fromJid;
    private String toJid;
    private String status = STATUS_SENT;
    private Date createdAt;
    private User user;

    private Message.Image image;
    private Message.Voice voice;

    public MessagePojo(){

    }

    public static MessagePojo getCopyFromMessage(Message message){
        MessagePojo instance = new MessagePojo();
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

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public Message.Voice getVoice() {
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

    public Message.Image getImage() {
        return this.image;
    }

    public void setImage(Message.Image image) {
        this.image = image;
    }

    public void setVoice(Message.Voice voice) {
        this.voice = voice;
    }

    @Override
    public int compareTo(MessagePojo o) {
        return this.getCreatedAt().compareTo(o.getCreatedAt());
    }
}
