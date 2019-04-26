package com.lingkarin.dev.chatapp.mvp.chatlist;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lingkarin.dev.chatapp.R;
import com.lingkarin.dev.chatapp.ChatApplication;
import com.lingkarin.dev.chatapp.chatkit.MessagesFixtures;
import com.lingkarin.dev.chatapp.data.models.Message;
import com.lingkarin.dev.chatapp.xmpp.XMPP;
import com.lingkarin.dev.chatapp.xmpp.XMPPAccount;
import com.lingkarin.dev.chatapp.xmpp.XMPPConnectionManager;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.minidns.dnsserverlookup.android21.AndroidUsingLinkProperties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        new Thread(new Runnable() {
            @Override
            public void run() {

                AndroidUsingLinkProperties.setup(getApplicationContext());

                final XMPPTCPConnection mConnection = XMPP.getInstance().getConnection(ChatListActivity.this);

                try {
                    mConnection.connect();
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "ERROR Auth" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (false) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Authenticated", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    try {
                        mConnection.login();

                        if (mConnection.isAuthenticated()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Authenticated", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "not Authenticated", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "ERROR Auth" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    }
                });


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "CREATE ACCOUNTTTT", Toast.LENGTH_SHORT).show();
                    }
                });

                AccountManager accountManager = AccountManager.getInstance(mConnection);
                Map<String, String> attributes = new HashMap<>();
                attributes.put("name", "full_name");
                attributes.put("email", "email");
                try {
                    if (accountManager.supportsAccountCreation()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "CREATE ACCOUNT", Toast.LENGTH_SHORT).show();
                            }
                        });
                        accountManager.sensitiveOperationOverInsecureConnection(true);
                        accountManager.createAccount(Localpart.fromOrThrowUnchecked("username"),"password", attributes);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "CREATE ACCOUNT FINISH", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "CREATE ACCOUNT FORBIDDEN", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (final Exception e) {
                    //TODO : Case 409 or Message conflict is the case of username exist handle the case
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "EXCEPTION BROO \n" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("SEBEL", "EXCEPTION BROO \n" + e.toString());
                        }
                    });
                }

                try {
                    Roster roster = XMPP.getInstance().getRoster();
                    Collection<RosterEntry> entries = roster.getEntries();
                    Presence presence;

                    for(RosterEntry entry : entries) {
                        presence = roster.getPresence(JidCreate.bareFrom(entry.getJid()));

                        Log.d("ROSTER", entry.getJid().toString());
                        Log.d("ROSTER", presence.getType().name());
                        Log.d("ROSTER", presence.getStatus());
                    }

                } catch (XMPPException | XmppStringprepException e) {
                    e.printStackTrace();
                }
            }
        }).start();


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
