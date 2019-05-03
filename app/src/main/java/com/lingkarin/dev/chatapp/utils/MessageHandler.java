package com.lingkarin.dev.chatapp.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.delay.packet.DelayInformation;

import java.util.List;

public class MessageHandler implements StanzaListener {
    Context context;
//    DatabaseHelper db;
    private static String TAG = "MSGHANDLER";

    public MessageHandler(Context paramContext) {
        this.context = paramContext;
//        this.db = DatabaseHelper.getInstance(paramContext);
    }

    public void processPacket(Stanza paramPacket) {
//        MessageItem messageItem = null;

        if ((paramPacket instanceof Message)) {
            Message localMessage = (Message) paramPacket;

            if ((localMessage.getBody() != null)
                    && (!localMessage.getBody().equals(""))) {

//                messageItem = new MessageItem();
//                messageItem.opponent = StringUtils.maybeToString(localMessage.getFromJid().getLocalpartOrNull());
//                messageItem.timestamp = Calendar.getInstance()
//                        .getTimeInMillis();
//                messageItem.message = localMessage.getBody();
//                messageItem.isNewMessage = true;

                Log.d("MYAPP", "RECEIVED MESSAGE : " + localMessage.getBody());


                boolean newMessage = true;
                List<ExtensionElement> extnElements = localMessage.getExtensions();
                for (ExtensionElement ext : extnElements) {
                    if (ext instanceof DelayInformation) {
                        newMessage = false;
                        DelayInformation delay = (DelayInformation) ext;
                    }

                }
                // if (!newMessage) {
                // return;
                // }

                // if (localMessage.getBody().equals("ANONYMOUS REQUEST")) {
                // ContactItem contact = new ContactItem();
                // contact.username =
                // StringUtils.parseName(localMessage.getFromJid());
                // VCard card = new VCard();
                // try {
                // card.load(XMPP.getInstance().getConnection(context),
                // StringUtils.parseBareAddress(localMessage.getFromJid()));
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // if (card == null || card.getNickName() == null) {
                // contact.displayName = "Anonymous user";
                // } else {
                // contact.displayName = card.getNickName();
                // }
                // contact.isShowHome = true;
                // contact.isRegistered = true;
                // contact.anonymous = true;
                // DatabaseHelper.getInstance(context).updateContact(contact);
                // // return;
                // }
                // boolean messageNew = true;
                List<ExtensionElement> paramPacketExtension = paramPacket.getExtensions();
                for (ExtensionElement ext : ((Message) paramPacket)
                        .getExtensions()) {
                    if (ext instanceof DelayInformation) {
                        newMessage = false;
                        DelayInformation delay = (DelayInformation) ext;
                    }
                }
                if (!newMessage) {
                    return;
                }
                // }

                if (!isAppRunning(context)) {

                    Notifications.showIncomingMessageNotification(
                            context,
                            localMessage.getType() == Message.Type.groupchat,
//                            localMessage.getFromJid(),
                            StringUtils.maybeToString(localMessage.getFrom().getLocalpartOrNull()),
                            localMessage.getBody(), StringUtils.maybeToString(localMessage.getFrom().getLocalpartOrNull()));

//                    StringUtils.maybeToString(localMessage.getFromJid().getLocalpartOrNull()),
////                                    StringUtils.parseBareAddress(localMessage.getBody()), StringUtils.parseBareAddress(localMessage.getFromJid()));
//                            localMessage.getBody(), StringUtils.maybeToString(localMessage.getFromJid().getLocalpartOrNull()));

                    Log.e("tag", "message is not AppRunning context chat"
                            + localMessage);

                } else {
                    Log.e("tag", "message came isAppRunning context"
                            + localMessage + ",");

                }

            }
        }
    }

    public static boolean isAppRunning(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);
        // The first in the list of RunningTasks is always the foreground task.
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo.topActivity
                .getPackageName();
        return foregroundTaskPackageName.equals(context.getPackageName());
    }

    @Override
    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
        processPacket(packet);
    }
}