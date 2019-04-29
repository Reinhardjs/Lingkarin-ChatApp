package com.lingkarin.dev.chatapp.mvp.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.data.models.User;
import com.lingkarin.dev.chatapp.services.MyService;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import javax.inject.Inject;

public class ChatActivity extends AppCompatActivity
        implements MessageInput.InputListener, MessageInput.AttachmentsListener, MessageInput.TypingListener,
        MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    @Inject
    String sharedText;

    private MessagesList messagesList;
    private MessagesListAdapter messagesAdapter;

    private static final int TOTAL_MESSAGES_COUNT = 30;
    protected final String myUsername = "0";
    protected ImageLoader imageLoader;

    private int selectionCount;
    private Date lastLoadedDate;
    private String chatToUsername;

    TextView toolbarTitle, toolbarSubtitle;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        ChatApplication.getApplicationComponent().inject(this);

        this.messagesList = findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);


        chatToUsername = getIntent().getStringExtra("chatToUsername");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access to the custom title view
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarSubtitle = toolbar.findViewById(R.id.toolbar_subtitle);

        toolbarTitle.setText(chatToUsername);
        toolbarSubtitle.setText("Sedang mengetik...");
        toolbarSubtitle.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop(){
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onReceiveNewMessageEvent(Intent intent){
        switch (intent.getAction()){
            case MyService.NEW_MESSAGE:
                String from_jid = intent.getStringExtra(MyService.BUNDLE_FROM_JID);
                String messageBody = intent.getStringExtra(MyService.BUNDLE_MESSAGE_BODY);

                User user = new User(from_jid, from_jid, "http://i.imgur.com/Qn9UesZ.png", true);
                Message message = new Message(myUsername, user, messageBody);

                messagesAdapter.addToStart(message, true);
                break;
            case MyService.RECEIVE_ON_START_TYPING:
                toolbarSubtitle.setVisibility(View.VISIBLE);
                break;
            case MyService.RECEIVE_ON_STOP_TYPING:
                toolbarSubtitle.setVisibility(View.GONE);
                break;
        }
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
                Picasso.with(ChatActivity.this).load(url).into(imageView);
            }
        };

        messagesAdapter = new MessagesListAdapter<>(myUsername, imageLoader);
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
        User user = new User(myUsername, myUsername, "http://i.imgur.com/Qn9UesZ.png", true);
        Message message = new Message(myUsername, user, input.toString());

        messagesAdapter.addToStart(message, true);

        Intent sendMessageIntent = new Intent();
        sendMessageIntent.setAction(MyService.SEND_MESSAGE);
        sendMessageIntent.putExtra("body", input.toString());
        sendMessageIntent.putExtra("toJid", chatToUsername);
        EventBus.getDefault().post(sendMessageIntent);
        return true;
    }

    @Override
    public void onStartTyping() {
        Log.d("CHATACTIVITY", "OM START TYPING");

        Intent sendOnStartTyping = new Intent();
        sendOnStartTyping.setAction(MyService.SEND_ON_START_TYPING);
        sendOnStartTyping.putExtra("toJid", chatToUsername);
        EventBus.getDefault().post(sendOnStartTyping);
    }

    @Override
    public void onStopTyping() {
        Log.d("CHATACTIVITY", "OM STOP TYPING");

        Intent sendOnStopTyping = new Intent();
        sendOnStopTyping.setAction(MyService.SEND_ON_STOP_TYPING);
        sendOnStopTyping.putExtra("toJid", chatToUsername);
        EventBus.getDefault().post(sendOnStopTyping);
    }

    protected void loadMessages() {
        //imitation of internet connection
        new Handler().postDelayed(() -> {
//            ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
//            lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
//            messagesAdapter.addToEnd(messages, false);
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






//        AccountManager accountManager = AccountManager.getInstance(mConnection);
//        Map<String, String> attributes = new HashMap<>();
//        attributes.put("name", "full_name");
//        attributes.put("email", "email");
//        try {
//            if (accountManager.supportsAccountCreation()) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "CREATE ACCOUNT", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                accountManager.sensitiveOperationOverInsecureConnection(true);
//                accountManager.createAccount(Localpart.fromOrThrowUnchecked("username"),"password", attributes);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "CREATE ACCOUNT FINISH", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "CREATE ACCOUNT FORBIDDEN", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        } catch (final Exception e) {
//            //TODO : Case 409 or Message conflict is the case of username exist handle the case
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "EXCEPTION BROO \n" + e.toString(), Toast.LENGTH_SHORT).show();
//                    Log.d("SEBEL", "EXCEPTION BROO \n" + e.toString());
//                }
//            });
//        }
//
//        try {
//            Roster roster = XMPP.getInstance().getRoster();
//            Collection<RosterEntry> entries = roster.getEntries();
//            Presence presence;
//
//            for(RosterEntry entry : entries) {
//                presence = roster.getPresence(JidCreate.bareFrom(entry.getJid()));
//
//                Log.d("ROSTER", entry.getJid().toString());
//                Log.d("ROSTER", presence.getType().name());
//                Log.d("ROSTER", presence.getStatus());
//            }
//
//        } catch (XMPPException | XmppStringprepException e) {
//            e.printStackTrace();
//        }