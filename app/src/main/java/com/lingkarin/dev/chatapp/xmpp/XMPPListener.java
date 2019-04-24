package com.lingkarin.dev.chatapp.xmpp;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

public interface XMPPListener extends ConnectionListener, RosterLoadedListener, RosterListener, RosterEntries,
        PresenceEventListener, SubscribeListener, IncomingChatMessageListener, OutgoingChatMessageListener,
        ReceiptReceivedListener, ChatStateListener {
}
