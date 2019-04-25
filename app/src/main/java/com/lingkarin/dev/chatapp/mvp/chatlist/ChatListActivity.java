package com.lingkarin.dev.chatapp.mvp.chatlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.ChatApplication;

import javax.inject.Inject;

public class ChatListActivity extends AppCompatActivity {

    @Inject
    String sharedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        ChatApplication.getApplicationComponent().inject(this);

        Toast.makeText(getApplicationContext(), sharedText, Toast.LENGTH_LONG).show();
    }
}
