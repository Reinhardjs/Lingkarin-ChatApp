package com.lingkarin.dev.chatapp.mvp.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.data.Credential;
import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.mvp.chatlist.ChatListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.chatListButton)
    Button chatListButton;

    MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainPresenter = new MainPresenter(this);

        final Credential credential = ChatApplication.getAuthComponent().getCredential();

        chatListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), credential.getUsername(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                startActivity(intent);
            }
        });

    }
}
