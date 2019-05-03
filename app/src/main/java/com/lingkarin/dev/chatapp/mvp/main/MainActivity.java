package com.lingkarin.dev.chatapp.mvp.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.constants.Config;
import com.lingkarin.dev.chatapp.data.AppSettings;
import com.lingkarin.dev.chatapp.data.Credential;
import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.data.source.Repository;
import com.lingkarin.dev.chatapp.mvp.LoginActivity;
import com.lingkarin.dev.chatapp.mvp.chat.ChatActivity;
import com.lingkarin.dev.chatapp.mvp.groupchat.GroupCreateActivity;
import com.lingkarin.dev.chatapp.services.service.LiveAppService;
import com.lingkarin.dev.chatapp.services.service.MyService;
import com.lingkarin.dev.chatapp.utils.Utils;
import com.lingkarin.dev.chatapp.xmpp.XMPP;
import com.lingkarin.dev.chatapp.xmpp.XMPPChatHandler;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.textView) TextView textView;
    @BindView(R.id.chatTo) EditText chatToEditText;
    @BindView(R.id.chatButton) Button chatButton;
    @BindView(R.id.disconnect) Button disconnectButton;
    @BindView(R.id.checkUser) Button checkUserButton;
    @BindView(R.id.mucButton) Button mucButton;
    @BindView(R.id.broadcast) Button broadcastButton;

    MainPresenter mainPresenter;
    private Disposable internetDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        startService(new Intent(MainActivity.this, LiveAppService.class));

        mainPresenter = new MainPresenter(this);

        final Credential credential = ChatApplication.getAuthComponent().getCredential();

        chatButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("chatToUsername", chatToEditText.getText().toString());
            startActivity(intent);
        });

        disconnectButton.setOnClickListener(view -> {
            Intent disconnectIntent = new Intent();
            disconnectIntent.setAction(MyService.DISCONNECT);
            EventBus.getDefault().post(disconnectIntent);
        });

        checkUserButton.setOnClickListener(view -> {
            EntityFullJid entityFullJid = XMPP.getInstance().getConnection(getApplicationContext()).getUser();
            BareJid bareJid = entityFullJid.asBareJid();

            if (entityFullJid == null){
                Toast.makeText(getApplicationContext(), "No User Account", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), entityFullJid.toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), bareJid.toString(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), bareJid.getLocalpartOrNull(), Toast.LENGTH_SHORT).show();
            }

        });

        mucButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GroupCreateActivity.class);
            startActivity(intent);
        });

        broadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String groupName = chatToEditText.getText().toString();
                    EntityBareJid jid = JidCreate.entityBareFrom(groupName + "@" + "broadcast." + Config.XMPP_DOMAIN);
                    Message msg = new Message(jid, Message.Type.chat);
                    msg.setBody("BODY : " + jid.getLocalpart().asUnescapedString());
                    msg.setStanzaId(XMPPChatHandler.DELIVER_REQUEST);
                    msg.setFrom(JidCreate.from(AppSettings.getUserName(MainActivity.this) + "@"+ Config.XMPP_DOMAIN));
                    XMPP.getInstance().connection.sendStanza(msg);
                } catch (XmppStringprepException | SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Toast.makeText(getApplicationContext(), AppSettings.getUserName(getApplicationContext()), Toast.LENGTH_SHORT).show();

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    if (isConnected){
                        Utils.setInternetStatus(Utils.INTERNET_CONNECTED);
                    } else {
                        Utils.setInternetStatus(Utils.INTERNET_DISCONNECTED);
                    }

                    String message;
                    if (isConnected){
                        message = "Tersambung ke internet";
                    } else {
                        message = "Koneksi internet putus";
                    }

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

}