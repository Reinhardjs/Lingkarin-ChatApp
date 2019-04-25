package com.lingkarin.dev.chatapp.mvp.chatlist;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

public class ChatListActivity extends AppCompatActivity
        implements MessageInput.InputListener, MessageInput.AttachmentsListener, MessageInput.TypingListener,
        MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    @Inject
    String sharedText;

    private MessagesList messagesList;
    private MessagesListAdapter messagesAdapter;

    private static final int TOTAL_MESSAGES_COUNT = 30;
    protected final String senderId = "0";
    protected ImageLoader imageLoader;

    private int selectionCount;
    private Date lastLoadedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        ChatApplication.getApplicationComponent().inject(this);

        Toast.makeText(getApplicationContext(), sharedText, Toast.LENGTH_LONG).show();

        this.messagesList = findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        messagesAdapter.addToStart(MessagesFixtures.getTextMessage(), true);
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    private void initAdapter() {

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.with(ChatListActivity.this).load(url).into(imageView);
            }
        };

        messagesAdapter = new MessagesListAdapter<>(senderId, imageLoader);
        messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {

                    }
                });
        this.messagesList.setAdapter(messagesAdapter);
    }

    @Override
    public void onAddAttachments() {
//        messagesAdapter.addToStart(
//                MessagesFixtures.getImageMessage(), true);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        messagesAdapter.addToStart(
                MessagesFixtures.getTextMessage(input.toString()), true);
        return true;
    }

    @Override
    public void onStartTyping() {

    }

    @Override
    public void onStopTyping() {

    }

    protected void loadMessages() {
        new Handler().postDelayed(new Runnable() { //imitation of internet connection
            @Override
            public void run() {
                ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
                lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
                messagesAdapter.addToEnd(messages, false);
            }
        }, 1000);
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
//        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
//        this.selectionCount = count;
//        menu.findItem(R.id.action_delete).setVisible(count > 0);
//        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }
}
