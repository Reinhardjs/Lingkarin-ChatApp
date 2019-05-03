package com.lingkarin.dev.chatapp.mvp.chat.holders;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomOutcomingTextMessageViewHolder extends MessageHolders.OutcomingImageMessageViewHolder<Message> {

    private ImageView deliverReceiptIndicator;
    private TextView messageText;

    public CustomOutcomingTextMessageViewHolder(View itemView, Object payload){
        super(itemView, payload);
        deliverReceiptIndicator = itemView.findViewById(R.id.deliverReceiptIndicator);
        messageText = itemView.findViewById(R.id.messageText);
    }

    @Override
    public void onBind(Message message){
        super.onBind(message);

        String messageStatus = message.getMessageStatus();
        switch (messageStatus){
            case Message.STATUS_SENT:
                deliverReceiptIndicator
                        .setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.indicator_deliver),
                                PorterDuff.Mode.SRC_IN);
                break;
            case Message.STATUS_DELIVERED:
                deliverReceiptIndicator
                        .setColorFilter(ContextCompat.getColor(itemView.getContext(),android.R.color.black),
                                PorterDuff.Mode.SRC_IN);
                break;
            case Message.STATUS_RECEIVED:
                deliverReceiptIndicator
                        .setColorFilter(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark),
                                PorterDuff.Mode.SRC_IN);
                break;
        }

        if (messageStatus == Message.STATUS_SENT){

        } else {

        }

        if (message.getText() != null) {
            messageText.setText(message.getText());
        }

        //We can set click listener on view fromJid payload
        final Payload payload = (Payload) this.payload;

    }

    public static class Payload {
        public OnAvatarClickListener avatarClickListener;
    }

    public interface OnAvatarClickListener {
        void onAvatarClick();
    }
}