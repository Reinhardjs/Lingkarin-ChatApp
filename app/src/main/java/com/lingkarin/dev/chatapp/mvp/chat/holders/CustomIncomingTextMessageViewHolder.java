package com.lingkarin.dev.chatapp.mvp.chat.holders;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.constants.Config;
import com.lingkarin.dev.chatapp.data.AppSettings;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.data.source.Repository;
import com.lingkarin.dev.chatapp.xmpp.XMPP;
import com.stfalcon.chatkit.messages.MessageHolders;

import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;

import static com.lingkarin.dev.chatapp.xmpp.XMPPChatHandler.RECEIPT_RESPONSE;

public class CustomIncomingTextMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    private ImageView userAvatar;
    private TextView text;

    public CustomIncomingTextMessageViewHolder(View itemView, Object payload){
        super(itemView, payload);
        init(itemView);
    }

    @Override
    public void onBind(Message message){
        super.onBind(message);

        //We can set click listener on view fromJid payload
        final CustomIncomingTextMessageViewHolder.Payload payload = (CustomIncomingTextMessageViewHolder.Payload) this.payload;
        userAvatar.setOnClickListener(view -> {
            if (payload != null && payload.avatarClickListener != null) {
                payload.avatarClickListener.onAvatarClick();
            }
        });

        if (userAvatar != null) {
            boolean isAvatarExists = imageLoader != null
                    && message.getUser().getAvatar() != null
                    && !message.getUser().getAvatar().isEmpty();

            userAvatar.setVisibility(isAvatarExists ? View.VISIBLE : View.GONE);
            if (isAvatarExists) {
                imageLoader.loadImage(userAvatar, message.getUser().getAvatar(), null);
            }
        }

        if (bubble != null) {
            bubble.setSelected(isSelected());
        }

        if (text != null) {
            text.setText(message.getText());
        }

        if (message.status.equals(Message.STATUS_DELIVERED)){
            // update firestore message, field status nya menjadi STATUS_RECEIVED
            Repository.getInstance(ChatApplication.getApplicationComponent().getContext())
                    .updateMessageStatusFireStore(message.getId(), Message.STATUS_RECEIVED, () -> {
                        // onUpdateMessageStatusSuccess
                    });


            String messageId = message.getId(); // ini adalah id message nya
            org.jivesoftware.smack.packet.Message received = new org.jivesoftware.smack.packet.Message();
            received.setBody(messageId);
            received.setStanzaId(RECEIPT_RESPONSE);


            try {
                // Ini adalah jid opponent, untuk membalas response kalau message nya udah received
                Jid target = JidCreate.from(message.fromJid + "@"+ Config.XMPP_DOMAIN);
                Jid from = JidCreate.from(message.toJid + "@"+ Config.XMPP_DOMAIN);

                received.addExtension(new DeliveryReceipt(Message.STATUS_RECEIVED));
                received.setTo(target);
                received.setFrom(from);
                XMPP.getInstance().connection.sendStanza(received);
            } catch(Exception ex) {
                Log.d("MYSERVICE", "RECEIPT ERRRORRR : " + ex.toString());
            }


        }
    }

    private void init(View itemView){
        this.userAvatar = itemView.findViewById(R.id.messageUserAvatar);
        this.text = itemView.findViewById(R.id.messageText);
    }

    public static class Payload {
        public OnAvatarClickListener avatarClickListener;
    }

    public interface OnAvatarClickListener {
        void onAvatarClick();
    }
}