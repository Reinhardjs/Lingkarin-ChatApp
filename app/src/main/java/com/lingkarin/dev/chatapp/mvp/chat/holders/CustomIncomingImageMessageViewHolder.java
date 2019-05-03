package com.lingkarin.dev.chatapp.mvp.chat.holders;

import android.view.View;

import com.lingkarin.dev.chatapp.data.models.Message;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomIncomingImageMessageViewHolder extends MessageHolders.IncomingImageMessageViewHolder<Message> {
    public CustomIncomingImageMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }
}
