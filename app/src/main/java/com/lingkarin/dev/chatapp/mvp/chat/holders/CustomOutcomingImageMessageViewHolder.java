package com.lingkarin.dev.chatapp.mvp.chat.holders;

import android.view.View;

import com.lingkarin.dev.chatapp.data.models.Message;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomOutcomingImageMessageViewHolder extends MessageHolders.OutcomingImageMessageViewHolder<Message> {
    public CustomOutcomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }
}
