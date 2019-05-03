package com.lingkarin.dev.chatapp.mvp.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.data.AppSettings;
import com.lingkarin.dev.chatapp.data.models.DeliveryReceiptData;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.data.models.User;
import com.lingkarin.dev.chatapp.data.source.RemoteDataSource;
import com.lingkarin.dev.chatapp.data.source.Repository;
import com.lingkarin.dev.chatapp.data.source.persistence.ChatAppLocal;
import com.lingkarin.dev.chatapp.mvp.chat.holders.CustomIncomingImageMessageViewHolder;
import com.lingkarin.dev.chatapp.mvp.chat.holders.CustomIncomingTextMessageViewHolder;
import com.lingkarin.dev.chatapp.mvp.chat.holders.CustomOutcomingImageMessageViewHolder;
import com.lingkarin.dev.chatapp.mvp.chat.holders.CustomOutcomingTextMessageViewHolder;
import com.lingkarin.dev.chatapp.services.service.MyService;
import com.lingkarin.dev.chatapp.utils.Utils;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ChatActivity extends AppCompatActivity
        implements MessageInput.InputListener, MessageInput.AttachmentsListener, MessageInput.TypingListener,
        MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    @Inject
    String sharedText;

    private MessagesList messagesList;
    private MessagesListAdapter messagesAdapter;

    private static final int TOTAL_MESSAGES_COUNT = 30;


    public static String myUsername = "0";
    public static String chatToUsername;


    protected ImageLoader imageLoader;

    private int selectionCount;
    private Date lastLoadedDate;

    TextView toolbarTitle, toolbarSubtitle;
    ChatAppLocal mLocalRepository;

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private static final String TAG = "MYSERVICE";
    private Repository mRepository;

    public HashMap<String, Message> messagesReferenceMap = new HashMap<>();

    @Override
    public void onStop(){
        super.onStop();

        mDisposable.clear();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        myUsername = AppSettings.getUserName(this);
        mLocalRepository = ChatAppLocal.getInstance(getApplicationContext());
        mRepository = Repository.getInstance(getApplicationContext());

        init(getIntent());
    }

    private void init(Intent intent){
        ChatApplication.getApplicationComponent().inject(this);
        chatToUsername = intent.getStringExtra("chatToUsername");

        this.messagesList = findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setTypingListener(this);
        input.setAttachmentsListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Get access toJid the custom title view
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarSubtitle = toolbar.findViewById(R.id.toolbar_subtitle);

        toolbarTitle.setText(chatToUsername);
        toolbarSubtitle.setText("Sedang mengetik...");
        toolbarSubtitle.setVisibility(View.GONE);
    }

    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onReceiveNewMessageEvent(Intent intent){
        switch (intent.getAction()){
            case MyService.NEW_MESSAGE:
                String from_jid = intent.getStringExtra(MyService.BUNDLE_FROM_JID);
                String messageBody = intent.getStringExtra(MyService.BUNDLE_MESSAGE_BODY);
                Message message = Utils.jsonToMessage(messageBody);

                mLocalRepository.insertMessage(message);
                messagesReferenceMap.put(message.getId(), message);
                messagesAdapter.addToStart(message, true);
                break;
            case MyService.RECEIVE_ON_START_TYPING:
                toolbarSubtitle.setVisibility(View.VISIBLE);
                break;
            case MyService.RECEIVE_ON_STOP_TYPING:
                toolbarSubtitle.setVisibility(View.GONE);

                break;
            case MyService.STATUS_DELIVERED:
                DeliveryReceiptData data = (DeliveryReceiptData) intent.getSerializableExtra(DeliveryReceiptData.class.getSimpleName());
                Toast.makeText(getApplicationContext(), "Delivered for " + data.getMessageId() + ", " + data.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Delivered for msgid: " + data.getMessageId() + ",\n " +
                        "FROM : " + data.fromJid  + ",\n " +
                        "TO : " + data.toJid  + ",\n " +
                        "STATUS : " + data.status  + ",\n ");

                Objects.requireNonNull(messagesReferenceMap.get(data.getMessageId())).setStatus(Message.STATUS_DELIVERED);
                messagesAdapter.notifyDataSetChanged();
                break;
            case MyService.STATUS_RECEIVED:
                DeliveryReceiptData data2 = (DeliveryReceiptData) intent.getSerializableExtra(DeliveryReceiptData.class.getSimpleName());
                Toast.makeText(getApplicationContext(), "Received for " + data2.getMessageId() + ", " + data2.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Received for msgid: " + data2.getMessageId() + ",\n " +
                        "FROM : " + data2.fromJid  + ",\n " +
                        "TO : " + data2.toJid  + ",\n " +
                        "STATUS : " + data2.status  + ",\n ");

                Objects.requireNonNull(messagesReferenceMap.get(data2.getMessageId())).setStatus(Message.STATUS_RECEIVED);
                messagesAdapter.notifyDataSetChanged();
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

        //We can pass any data toJid ViewHolder with payload
        CustomIncomingTextMessageViewHolder.Payload payload = new CustomIncomingTextMessageViewHolder.Payload();
        //For example click listener
        payload.avatarClickListener = new CustomIncomingTextMessageViewHolder.OnAvatarClickListener() {
            @Override
            public void onAvatarClick() {
                Toast.makeText(ChatActivity.this,
                        "Text message avatar clicked", Toast.LENGTH_SHORT).show();
            }
        };

        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message,
                        payload)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message)
                .setIncomingImageConfig(
                        CustomIncomingImageMessageViewHolder.class,
                        R.layout.item_incoming_image_message)
                .setOutcomingImageConfig(
                        CustomOutcomingImageMessageViewHolder.class,
                        R.layout.item_outcoming_image_message);

        messagesAdapter = new MessagesListAdapter<>(myUsername, holdersConfig, imageLoader);
        messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                (MessagesListAdapter.OnMessageViewClickListener<Message>) (view, message) -> {

                });
        this.messagesList.setAdapter(messagesAdapter);

//        List<Message> messages = mLocalRepository.getMessages();
//        Toast.makeText(getApplicationContext(), "SIZE : " + messages.size(), Toast.LENGTH_SHORT).show();
//        messagesAdapter.addToEnd(messages, true);

        mRepository.getMessagesFirestore(myUsername, chatToUsername, new RemoteDataSource.GetMessagesCallback() {
            @Override
            public void onGetMessagesSuccess(List<Message> messageList) {
//                messagesAdapter.addToEnd((List) messagesList, true);
                for (Message message : messageList){
                    messagesReferenceMap.put(message.getId(), message);
                    messagesAdapter.addToStart(message, true);
                }
            }
        });

    }

    @Override
    public void onAddAttachments() {
//        messagesAdapter.addToStart(
//                MessagesFixtures.getImageMessage(), true);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        User user = new User(myUsername, myUsername, "http://i.imgur.com/Qn9UesZ.png", true);
        Message message = new Message(new Date().toString(), user, input.toString());
        message.setFromJid(myUsername);
        message.setToJid(chatToUsername);
        message.setStatus(Message.STATUS_SENT);

        mRepository.insertMessageFirestore(message, new RemoteDataSource.InsertMessageCallback() {
            @Override
            public void onInsertMessageSuccess() {
                // On Insert success callback
            }

            @Override
            public void onIdGenerated(String id) {
                message.setId(id);
                mLocalRepository.insertMessage(message);
                messagesReferenceMap.put(message.getId(), message);
                messagesAdapter.addToStart(message, true);

                Intent sendMessageIntent = new Intent();
                sendMessageIntent.setAction(MyService.SEND_MESSAGE);
                sendMessageIntent.putExtra("body", message);
                sendMessageIntent.putExtra("toJid", chatToUsername);
                EventBus.getDefault().post(sendMessageIntent);
            }
        });

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

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
//        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
//        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
//            loadMessages();
//        }
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