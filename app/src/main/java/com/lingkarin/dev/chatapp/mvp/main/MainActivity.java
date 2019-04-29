package com.lingkarin.dev.chatapp.mvp.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.data.Credential;
import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.mvp.chat.ChatActivity;
import com.lingkarin.dev.chatapp.mvp.groupchat.GroupCreateActivity;
import com.lingkarin.dev.chatapp.services.MyService;
import com.lingkarin.dev.chatapp.xmpp.XMPP;

import org.greenrobot.eventbus.EventBus;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityFullJid;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.textView) TextView textView;
    @BindView(R.id.username) EditText usernameEditText;
    @BindView(R.id.password) EditText passwordEditText;
    @BindView(R.id.chatTo) EditText chatToEditText;
    @BindView(R.id.chatButton) Button chatButton;
    @BindView(R.id.disconnect) Button disconnectButton;
    @BindView(R.id.checkUser) Button checkUserButton;
    @BindView(R.id.mucButton) Button mucButton;

    MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainPresenter = new MainPresenter(this);

        final Credential credential = ChatApplication.getAuthComponent().getCredential();

        chatButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("chatToUsername", chatToEditText.getText().toString());
                startActivity(intent);


                Intent connectIntent = new Intent();
                connectIntent.setAction(MyService.START_CONNECT);
                connectIntent.putExtra("username", usernameEditText.getText().toString());
                connectIntent.putExtra("password", passwordEditText.getText().toString());

                EventBus.getDefault().post(connectIntent);
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
                Toast.makeText(getApplicationContext(), bareJid.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), bareJid.getLocalpartOrNull(), Toast.LENGTH_SHORT).show();
            }

        });

        mucButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GroupCreateActivity.class);
            startActivity(intent);
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
