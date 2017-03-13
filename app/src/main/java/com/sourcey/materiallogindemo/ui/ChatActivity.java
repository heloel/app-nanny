package com.sourcey.materiallogindemo.ui;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.Utils.ParseConstants;
import com.sourcey.materiallogindemo.adapters.ChatListAdapter;
import com.sourcey.materiallogindemo.view.Message;

import butterknife.ButterKnife;
import butterknife.Bind;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    @Bind(R.id.etMessage)
    EditText etMessage;
    @Bind(R.id.btSend)
    Button btSend;
    @Bind(R.id.lvChat)
    ListView lvChat;

    ArrayList<Message> mMessages;
    ChatListAdapter mAdapter;
    boolean mFirstLoad;

    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    static final int POLL_INTERVAL = 100;

    Handler handler = new Handler();
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupMessagePosting();

        handler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
    }

    private void setupMessagePosting(){

        mMessages = new ArrayList<>();
        lvChat.setTranscriptMode(1);
        mFirstLoad = true;

        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatListAdapter(ChatActivity.this, userId, mMessages);

        lvChat.setAdapter(mAdapter);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = etMessage.getText().toString();
                ParseObject message = ParseObject.create("Message");
                message.put(ParseConstants.KEY_ID_USER, userId);
                message.put(ParseConstants.KEY_BODY, data);
                message.put(ParseConstants.KEY_USER_RELATION, ParseUser.getCurrentUser());

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            refreshMessages();
                        }
                    }
                });
                etMessage.setText("");
            }
        });
    }

    private void refreshMessages() {

        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByAscending("createAt");

        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if(e == null){
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged();

                    if(mFirstLoad){
                        lvChat.setSelection(mAdapter.getCount() -1);
                        mFirstLoad = false;
                    }
                }
            }
        });
    }
}
