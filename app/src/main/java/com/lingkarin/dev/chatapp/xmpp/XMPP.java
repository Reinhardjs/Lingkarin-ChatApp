package com.lingkarin.dev.chatapp.xmpp;

import android.content.Context;
import android.util.Log;

import com.lingkarin.dev.chatapp.constants.Config;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;

public class XMPP {

    public static String HOST = Config.XMPP_DOMAIN;
//    public static final String LIVESERVICE = "liveappgroup@liveapp." + HOST;

    public static final int PORT = Config.XMPP_PORT;
    private static XMPP instance;
    public XMPPTCPConnection connection;
    private static String TAG = "SAMPLE-XMPP";

    private static String username = "admin";
    // private static String password = "P@ssw0rd$ad1z";
    private static String password = "password";

    private XMPPTCPConnectionConfiguration buildConfiguration() throws XmppStringprepException {
        XMPPTCPConnectionConfiguration.Builder builder =
                XMPPTCPConnectionConfiguration.builder();

        builder.setUsernameAndPassword(username, password);
        builder.setAuthzid(getEntityBareJid());
        builder.setXmppDomain(Config.XMPP_DOMAIN);
        builder.setHost(Config.XMPP_DOMAIN);
        builder.setPort(Config.XMPP_PORT);
        builder.setResource(Resourcepart.from(Config.XMPP_RESOURCE));
        builder.setCompressionEnabled(true);
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        builder.setKeystoreType("AndroidCAStore");
        builder.setKeystorePath(null);
        builder.setSendPresence(true);
//        try {
//            SSLContext ssl = SSLContext.getInstance("TLS");
//            ssl.init(null, new TrustManager[]{new TLSUtils.AcceptAllTrustManager()}, null);
//            ssl.getServerSessionContext().setSessionTimeout(10 * 1000);
//            builder.setCustomSSLContext(ssl);
//        } catch (Exception e) {
//            Log.e(TAG, "getConnectionConfig:" + "setCustomSSLContext", e);
//        }

        return builder.build();
    }

    private EntityBareJid getEntityBareJid() {
        try {
            return JidCreate.entityBareFrom(
                    Localpart.from(username),
                    Domainpart.from(Config.XMPP_DOMAIN)
            );
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setAccount(String username, String password){
        this.username = username;
        this.password = password;
    }

    public XMPPTCPConnection getConnection(Context context) {
        Log.i(TAG, "Inside getConnection");
        if ((this.connection == null) || (!this.connection.isConnected())) {
            try {
                this.connection = connect();

                //                this.connection.login(AppSettings.getUser(context), AppSettings.getPassword(context));
//                Log.i(TAG, "inside XMPP getConnection method after login");
//                context.sendBroadcast(new Intent("liveapp.loggedin"));
            } catch (XMPPException localXMPPException) {
            } catch (SmackException e) {
                Log.d("SAMPLE-XMPP", "ERROR 1 " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("SAMPLE-XMPP", "ERROR 2 " + e.toString());
                e.printStackTrace();
            } catch (InterruptedException e) {
                Log.d("SAMPLE-XMPP", "ERROR 3 " + e.toString());
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                Log.d("SAMPLE-XMPP", "ERROR 4 " + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("SAMPLE-XMPP", "ERROR" + e.toString());
                e.printStackTrace();
            }
        }
        Log.i(TAG, "Inside getConnection - Returning connection");
        return connection;
    }

    private XMPPTCPConnection connect() throws XMPPException, SmackException, IOException, InterruptedException {
        Log.i(TAG, "Getting XMPP Connect");
        if ((this.connection != null) && (this.connection.isConnected())) {
            Log.i(TAG, "Returning already existing connection");
            return this.connection;
        }
        Log.i(TAG, "Connection not found, creating new one");
        long l = System.currentTimeMillis();
        XMPPTCPConnectionConfiguration config = buildConfiguration();
        SmackConfiguration.DEBUG = true;

        if (connection == null) {
            this.connection = new XMPPTCPConnection(config);
        }

        this.connection.setUseStreamManagement(false);

        if (!connection.isConnected()){
            this.connection.connect();
        }
//        Roster roster = Roster.getInstanceFor(connection);
//
//        if (!roster.isLoaded())
//            roster.reloadAndWait();
//
//        Log.i(TAG, "Time taken in first time connect: " + (System.currentTimeMillis() - l));
//        roster = Roster.getInstanceFor(connection);
//
//        roster.addRosterListener(new RosterListener() {
//            @Override
//            public void entriesAdded(Collection<Jid> addresses) {
//                Log.d("deb", "ug");
//            }
//
//            @Override
//            public void entriesUpdated(Collection<Jid> addresses) {
//                Log.d("deb", "ug");
//            }
//
//            @Override
//            public void entriesDeleted(Collection<Jid> addresses) {
//                Log.d("deb", "ug");
//            }
//
//            @Override
//            public void presenceChanged(Presence presence) {
//                Log.d("deb", "ug");
//            }
//        });

        Log.d(TAG, "SUCCESS");

        return this.connection;
    }

    public static XMPP getInstance() {
        if (instance == null) {
            instance = new XMPP();
        }
        return instance;
    }

    public void close() {
        Log.i(TAG, "inside XMPP close method");
        if (this.connection != null) {
            this.connection.disconnect();
        }
        instance = null;
    }

    public Roster getRoster() throws XMPPException {
        XMPPTCPConnection connection = null;
        Roster roster = null;
        try {
            connection = connect();
            roster = Roster.getInstanceFor(connection);
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return roster;
    }


    public boolean isConnected() {
        return (this.connection != null) && (this.connection.isConnected());
    }

    public void login(String user, String pass, String username)
            throws XMPPException, SmackException, IOException, InterruptedException {
        Log.i(TAG, "inside XMPP getlogin Method");
        long l = System.currentTimeMillis();
        XMPPTCPConnection connect = connect();
        if (connect.isAuthenticated()) {
            Log.i(TAG, "User already logged in");
            return;
        }

        Log.i(TAG, "Time taken to connect: " + (System.currentTimeMillis() - l));

        l = System.currentTimeMillis();
        connect.login(user, pass);
        Log.i(TAG, "Time taken to login: " + (System.currentTimeMillis() - l));

        Log.i(TAG, "login step passed");

        Presence p = new Presence(Presence.Type.available);
        p.setMode(Presence.Mode.available);
        p.setPriority(24);
        p.setFrom(connect.getUser());

//        if (status != null) {
//            p.setStatus(status.toJSON());
//        } else {
//            p.setStatus(new StatusItem().toJSON());
//        }

//        p.setTo("");
        VCard ownVCard = new VCard();
        ownVCard.load(connect);
        ownVCard.setNickName(username);
        ownVCard.save(connect);

//        PingManager pingManager = PingManager.getInstanceFor(connect);
//        pingManager.setPingInterval(150000);
//        connect.sendPacket(p);


    }

    public void register(String user, String pass) throws XMPPException, SmackException.NoResponseException, SmackException.NotConnectedException {
        Log.i(TAG, "inside XMPP register method, " + user + " : " + pass);
        long l = System.currentTimeMillis();
        try {
            AccountManager accountManager = AccountManager.getInstance(connect());
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(Localpart.from(user), pass);
        } catch (SmackException e) {
            e.printStackTrace();
            Log.d(TAG, "ERROR 1 : " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "ERROR 2 : " + e.toString());
        } catch (InterruptedException e) {
            Log.d(TAG, "ERROR 3 : " + e.toString());
            e.printStackTrace();
        }
        Log.i(TAG, "Time taken to register: " + (System.currentTimeMillis() - l));
    }
}