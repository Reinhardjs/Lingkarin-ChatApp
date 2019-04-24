package com.lingkarin.dev.chatapp.xmpp;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jxmpp.jid.EntityJid;

public class XMPPMUC implements InvitationListener {

    private static final String TAG = XMPPMUC.class.getSimpleName();

    private static XMPPMUC mXMPPMUC;
    private AbstractXMPPConnection mConnection;
    private MultiUserChatManager mMultiUserChatManager;

    @NonNull
    public static XMPPMUC getInstance(@NonNull AbstractXMPPConnection connection) {
        if (mXMPPMUC == null) {
            mXMPPMUC = new XMPPMUC(connection);
        }

        return mXMPPMUC;
    }

    private XMPPMUC(AbstractXMPPConnection connection) {
        mConnection = connection;
        init();
    }

    @Override
    public void invitationReceived(XMPPConnection conn, MultiUserChat room, EntityJid from,
                                   String reason, String password, Message message,
                                   MUCUser.Invite invitation) {
        Log.d(TAG,"InvitationListener(invitationReceived): from:" + from.asEntityBareJidString()
                + ", room:" + room.getSubject() + ", reason:" + reason + ", password:" + password
                + ", message:" + message.toString());
    }

    private void init() {
        mMultiUserChatManager = MultiUserChatManager.getInstanceFor(mConnection);
        setListener();
    }

    private void setListener() {
        mMultiUserChatManager.addInvitationListener(this);
    }

    private void removeListener() {
        mMultiUserChatManager.removeInvitationListener(this);
    }
}
