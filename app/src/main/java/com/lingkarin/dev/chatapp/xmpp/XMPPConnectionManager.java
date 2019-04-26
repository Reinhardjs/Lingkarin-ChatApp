package com.lingkarin.dev.chatapp.xmpp;

import android.support.annotation.NonNull;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;

import java.io.IOException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class XMPPConnectionManager implements ConnectionListener, ReconnectionListener {

    private static final String TAG = XMPPConnectionManager.class.getSimpleName();

    private static XMPPConnectionManager mXMPPConnectionManager;
    private XMPPAccount mXMPPAccount;
    private AbstractXMPPConnection mConnection;
    private ReconnectionManager mReconnectionManager;

    @NonNull
    public static XMPPConnectionManager getInstance(@NonNull XMPPAccount xmppAccount) {
        if (mXMPPConnectionManager == null) {
            mXMPPConnectionManager = new XMPPConnectionManager(xmppAccount);
        }

        return mXMPPConnectionManager;
    }

    private XMPPConnectionManager(XMPPAccount xmppAccount) {
        mXMPPAccount = xmppAccount;
        init();
    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.d(TAG, "ConnectionListener:" + "connected");
        try {
            login();
        } catch (Exception e) {
            Log.e(TAG, "login(failure):", e);
        }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d(TAG, "ConnectionListener:" + "authenticated");
        XMPPRosterManager.getInstance(mConnection);
        XMPPChatManager.getInstance(mConnection);
    }

    @Override
    public void connectionClosed() {
        Log.d(TAG, "ConnectionListener:" + "connectionClosed");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.e(TAG, "ConnectionListener:" + "connectionClosedOnError", e);
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.d(TAG, "ReconnectionListener:" + "reconnectingIn " + seconds);
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.e(TAG, "ReconnectionListener:" + "reconnectionFailed", e);
    }

    private XMPPTCPConnectionConfiguration getConnectionConfig() {
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setUsernameAndPassword(mXMPPAccount.getUsername(), mXMPPAccount.getPassword());
        builder.setAuthzid(getEntityBareJid());
        builder.setXmppDomain(getEntityBareJid().asDomainBareJid());
        builder.setHost(mXMPPAccount.getHost());
        builder.setPort(mXMPPAccount.getPort());
        builder.setResource(Resourcepart.fromOrNull(mXMPPAccount.getResource()));
        builder.setCompressionEnabled(true);
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        builder.setKeystoreType("AndroidCAStore");
        builder.setKeystorePath(null);

        try {
            SSLContext ssl = SSLContext.getInstance("TLS");
            ssl.init(null, new TrustManager[]{new TLSUtils.AcceptAllTrustManager()}, null);
            ssl.getServerSessionContext().setSessionTimeout(10 * 1000);
            builder.setCustomSSLContext(ssl);
        } catch (Exception e) {
            Log.e(TAG, "getConnectionConfig:" + "setCustomSSLContext", e);
        }

        return builder.build();
    }

    private EntityBareJid getEntityBareJid() {
        return JidCreate.entityBareFrom(
                Localpart.fromOrNull(mXMPPAccount.getUsername()),
                Domainpart.fromOrNull(mXMPPAccount.getDomain())
        );
    }

    private void init() {
        if (mConnection != null) return;
        mConnection = new XMPPTCPConnection(getConnectionConfig());
        mReconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        mReconnectionManager.enableAutomaticReconnection();
        setListener();
    }

    private void setListener() {
        mConnection.addConnectionListener(this);
        mReconnectionManager.addReconnectionListener(this);
    }

    private void removeListener() {
        mConnection.removeConnectionListener(this);
        mReconnectionManager.removeReconnectionListener(this);
    }

    public void connect() throws SmackException, XMPPException, IOException, InterruptedException {
        if (!mConnection.isConnected()) mConnection.connect();
    }

    public boolean isConnected(){
        return mConnection.isConnected();
    }

    public void disconnect() {
        XMPPChatManager.getInstance(mConnection).removeListener();
        XMPPRosterManager.getInstance(mConnection).removeListener();
        mConnection.disconnect();
        removeListener();
        mXMPPConnectionManager = null;
    }

    private void login() throws SmackException, XMPPException, IOException, InterruptedException {
        if (!mConnection.isAuthenticated()) mConnection.login();
    }
}
