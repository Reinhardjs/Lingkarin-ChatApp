package com.lingkarin.dev.chatapp.mvp.groupchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.constants.Config;
import com.lingkarin.dev.chatapp.xmpp.XMPP;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupCreateActivity extends AppCompatActivity {

    @BindView(R.id.groupname) EditText mGroupChatNameET;
    @BindView(R.id.username) EditText usernameET;

    @BindView(R.id.createButton) Button groupCreateButton;
    @BindView(R.id.inviteButton) Button inviteButton;
    @BindView(R.id.joinButton) Button joinButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        ButterKnife.bind(this);

        XMPP xmppInstance = XMPP.getInstance();
        XMPPTCPConnection mConnection = xmppInstance.getConnection(getApplicationContext());
        String myUserNickname = xmppInstance.connection.getUser().getLocalpart().toString();

        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(mConnection);
        multiUserChatManager.addInvitationListener(invitationListener);

        groupCreateButton.setOnClickListener(view -> {

            try {
                EntityBareJid jid = JidCreate.entityBareFrom(mGroupChatNameET.getText().toString() + "@" + "conference." + Config.XMPP_DOMAIN);

                MultiUserChat mMultiUserChat = MultiUserChatManager.getInstanceFor(mConnection).getMultiUserChat(jid);
                mMultiUserChat.create(Resourcepart.from(myUserNickname));

                EntityFullJid entityFullJid = XMPP.getInstance().getConnection(getApplicationContext()).getUser();
                BareJid bareJid = entityFullJid.asBareJid();

                List<String> owners = new ArrayList<>();
                owners.add(bareJid.toString());

                Form form = mMultiUserChat.getConfigurationForm().createAnswerForm();
                form.setAnswer("muc#roomconfig_publicroom", true);
                form.setAnswer("muc#roomconfig_roomname", "room786");
                form.setAnswer("muc#roomconfig_roomowners", owners);
                form.setAnswer("muc#roomconfig_persistentroom", true);
                mMultiUserChat.sendConfigurationForm(form);

                mMultiUserChat.join(Resourcepart.from(myUserNickname));

                Log.d("GROUPACTIVITY","JOIN ROOM");

            } catch (XmppStringprepException | SmackException.NoResponseException |
                    XMPPException.XMPPErrorException | InterruptedException |
                    MultiUserChatException.MucAlreadyJoinedException |
                    SmackException.NotConnectedException |
                    MultiUserChatException.MissingMucCreationAcknowledgeException |
                    MultiUserChatException.NotAMucServiceException e) {
                e.printStackTrace();

                Log.d("GROUPACTIVITY", e.toString());
            }
        });

        inviteButton.setOnClickListener(view -> {
            listenInvite();
        });

        joinButton.setOnClickListener(view -> {
            EntityBareJid jid = null;
            try {
                jid = JidCreate.entityBareFrom(mGroupChatNameET.getText().toString() + "@" + "conference." + Config.XMPP_DOMAIN);
                MultiUserChat mMultiUserChat = MultiUserChatManager.getInstanceFor(mConnection).getMultiUserChat(jid);
                mMultiUserChat.join(Resourcepart.from(myUserNickname));
            } catch (XmppStringprepException | InterruptedException | XMPPException.XMPPErrorException | SmackException.NotConnectedException | SmackException.NoResponseException | MultiUserChatException.NotAMucServiceException e) {
                e.printStackTrace();
                Log.d("GROUPACTIVITY", e.toString());
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(XMPP.getInstance().getConnection(getApplicationContext()));
        multiUserChatManager.removeInvitationListener(invitationListener);
    }

    InvitationListener invitationListener = new InvitationListener() {
        @Override
        public void invitationReceived(XMPPConnection conn, MultiUserChat room, EntityJid inviter, String reason, String password, Message message, MUCUser.Invite invitation) {
            Log.d("GROUPACTIVITY", "InvitationListener(invitationReceived): from:" + inviter.asEntityBareJidString()
                    + ", room:" + room.getSubject() + ", reason:" + reason + ", password:" + password
                    + ", message:" + message.toString());
        }
    };

    private void listenInvite(){
        XMPP xmppInstance = XMPP.getInstance();
        XMPPTCPConnection mConnection = xmppInstance.getConnection(getApplicationContext());

        try {
            EntityBareJid jid = JidCreate.entityBareFrom(mGroupChatNameET.getText().toString() + "@" + "conference." + Config.XMPP_DOMAIN);
            MultiUserChat mMultiUserChat = MultiUserChatManager.getInstanceFor(mConnection).getMultiUserChat(jid);

            EntityBareJid toJID = JidCreate.entityBareFrom(usernameET.getText().toString() + "@" + Config.XMPP_DOMAIN);
            mMultiUserChat.invite(toJID, "Hello, i invited you to join the group chat :)");

            Log.d("GROUPACTIVITY", "Invited");
        } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
            Log.d("GROUPACTIVITY", e.toString());
        }
    }

    private void groupCreate(){
        XMPP xmppInstance = XMPP.getInstance();
        XMPPTCPConnection mConnection = xmppInstance.getConnection(getApplicationContext());

        try {
            EntityBareJid jid = JidCreate.entityBareFrom(mGroupChatNameET.getText().toString() + "@" + "conference." + Config.XMPP_DOMAIN);

            MultiUserChat mMultiUserChat = MultiUserChatManager.getInstanceFor(mConnection).getMultiUserChat(jid);
            mMultiUserChat.create(Resourcepart.from(String.valueOf(XMPP.getInstance().connection.getUser().getLocalpart())));

            EntityFullJid entityFullJid = XMPP.getInstance().getConnection(getApplicationContext()).getUser();
            BareJid bareJid = entityFullJid.asBareJid();

            List<String> owners = new ArrayList<>();
            owners.add(bareJid.toString());

            Form form = mMultiUserChat.getConfigurationForm().createAnswerForm();
            form.setAnswer("muc#roomconfig_publicroom", true);
            form.setAnswer("muc#roomconfig_roomname", "room786");
            form.setAnswer("muc#roomconfig_roomowners", owners);
            form.setAnswer("muc#roomconfig_persistentroom", true);
            mMultiUserChat.sendConfigurationForm(form);

            mMultiUserChat.join(Resourcepart.from(String.valueOf(XMPP.getInstance().connection.getUser().getLocalpart())));

            Log.d("GROUPACTIVITY","JOIN ROOM");

            MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(mConnection);
            multiUserChatManager.addInvitationListener(invitationListener);

        } catch (XmppStringprepException | SmackException.NoResponseException |
                XMPPException.XMPPErrorException | InterruptedException |
                MultiUserChatException.MucAlreadyJoinedException |
                SmackException.NotConnectedException |
                MultiUserChatException.MissingMucCreationAcknowledgeException |
                MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();

            Log.d("GROUPACTIVITY", e.toString());
        }
    }
}
