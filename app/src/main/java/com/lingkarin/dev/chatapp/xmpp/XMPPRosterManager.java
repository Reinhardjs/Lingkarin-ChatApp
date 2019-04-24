package com.lingkarin.dev.chatapp.xmpp;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;
import java.util.Collection;

public class XMPPRosterManager implements RosterLoadedListener, RosterListener, RosterEntries,
        PresenceEventListener, SubscribeListener {

    private static final String TAG = XMPPRosterManager.class.getSimpleName();

    private static XMPPRosterManager mXMPPRosterManager;
    private AbstractXMPPConnection mConnection;
    private Roster mRoster;
    private ArrayList<RosterEntry> mRosterList = new ArrayList<>();

    @NonNull
    public static XMPPRosterManager getInstance(@NonNull AbstractXMPPConnection connection) {
        if (mXMPPRosterManager == null) {
            mXMPPRosterManager = new XMPPRosterManager(connection);
        }

        return mXMPPRosterManager;
    }

    private XMPPRosterManager(AbstractXMPPConnection connection) {
        mConnection = connection;
        init();
    }

    @Override
    public void onRosterLoaded(Roster roster) {
        Log.d(TAG, "RosterLoadedListener(onRosterLoaded):" + roster.toString());
    }

    @Override
    public void onRosterLoadingFailed(Exception exception) {
        Log.e(TAG, "RosterLoadedListener(onRosterLoadingFailed):", exception);
    }

    @Override
    public void entriesAdded(Collection<Jid> addresses) {
        Log.d(TAG, "RosterListener(entriesAdded):" + addresses.toString());
    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {
        Log.d(TAG, "RosterListener(entriesUpdated):" + addresses.toString());
    }

    @Override
    public void entriesDeleted(Collection<Jid> addresses) {
        Log.d(TAG, "RosterListener(entriesDeleted):" + addresses.toString());
    }

    @Override
    public void rosterEntries(Collection<RosterEntry> rosterEntries) {
        Log.d(TAG, "RosterEntries(rosterEntries):" + rosterEntries.toString());
        mRosterList.addAll(rosterEntries);
    }

    @Override
    public void presenceChanged(Presence presence) {
        Log.d(TAG, "PresenceEventListener(presenceChanged):" + presence.toString());
    }

    @Override
    public void presenceAvailable(FullJid address, Presence presence) {
        Log.d(TAG, "PresenceEventListener(presenceAvailable):" + presence.toString());
    }

    @Override
    public void presenceUnavailable(FullJid address, Presence presence) {
        Log.d(TAG, "PresenceEventListener(presenceUnavailable):" + presence.toString());
    }

    @Override
    public void presenceError(Jid address, Presence presence) {
        Log.d(TAG, "PresenceEventListener(presenceError):" + presence.toString());
    }

    @Override
    public void presenceSubscribed(BareJid address, Presence presence) {
        Log.d(TAG, "PresenceEventListener(presenceSubscribed):" + presence.toString());
    }

    @Override
    public void presenceUnsubscribed(BareJid address, Presence presence) {
        Log.d(TAG, "PresenceEventListener(presenceUnsubscribed):" + presence.toString());
    }

    @Override
    public SubscribeAnswer processSubscribe(Jid from, Presence presence) {
        Log.d(TAG, "SubscribeListener(processSubscribe):" + presence.toString());
        return SubscribeAnswer.Approve;
    }

    private void init() {
        mRoster = Roster.getInstanceFor(mConnection);
        setListener();
    }

    private void setListener() {
        mRoster.addRosterLoadedListener(this);
        mRoster.getEntriesAndAddListener(this, this);
        mRoster.addPresenceEventListener(this);
        mRoster.addSubscribeListener(this);
    }

    public void removeListener() {
        mRoster.removeRosterLoadedListener(this);
        mRoster.removeRosterListener(this);
        mRoster.removePresenceEventListener(this);
        mRoster.removeSubscribeListener(this);
    }

    public ArrayList<RosterEntry> getRosterEntries()  {
        return mRosterList;
    }
}

