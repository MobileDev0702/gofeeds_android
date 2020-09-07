package com.stackrage.gofeds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatDetailActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";
    public static final String PREF_USERNAME = "PREFERENCE_USERNAME";

    private ImageView iv_back_btn;
    private TextView tv_username;
    private ImageView iv_send_btn;
    private EditText et_bottom_comment;

    private RecyclerView messageRecyclerView;
    private MessageAdapter messageAdapter;
    private ArrayList<MessageInfo> messageInfos = new ArrayList<>();

    private DatabaseReference dbRef;
    private String roomId, receiverId, receiverUser;
    private String myId;
    private Integer isUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        initComponent();
        initData();
        loadMessage();
        setMessageRecyclerView();
        onClickBackBtn();
        onClickSendBtn();
    }

    private void initComponent() {
        iv_back_btn = findViewById(R.id.iv_back_btn);
        tv_username = findViewById(R.id.tv_username);
        iv_send_btn = findViewById(R.id.iv_send_btn);
        et_bottom_comment = findViewById(R.id.et_bottom_comments);
        messageRecyclerView = findViewById(R.id.messages_view);
    }

    private void initData() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        roomId = getIntent().getStringExtra("RoomId");
        receiverId = getIntent().getStringExtra("ReceiverId");
        receiverUser = getIntent().getStringExtra("ReceiverUser");
        SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
        myId = idPref.getString("Id", "");
        tv_username.setText(receiverUser);
    }

    private void loadMessage() {
        dbRef.child("messages").child("conversations").child(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String msg = snapshot.child("message").getValue().toString();
                String senderId = snapshot.child("userId").getValue().toString();
                String msgId = snapshot.getKey();

                Map<String, Object> result = new HashMap<>();
                result.put("lastMessage", msg);
                result.put("lastMessageTimeStamp", ServerValue.TIMESTAMP);
                result.put("messageId",msgId);
                dbRef.child("messages").child("chatUsers").child(myId).child(roomId).updateChildren(result);

                Map<String, Object> resultOppo = new HashMap<>();
                resultOppo.put("lastMessage", msg);
                resultOppo.put("lastMessageTimeStamp", ServerValue.TIMESTAMP);
                resultOppo.put("messageId",msgId);
                dbRef.child("messages").child("chatUsers").child(receiverId).child(roomId).updateChildren(resultOppo);

                if (myId.equals(senderId)) {
                    isUser = 0;
                } else {
                    isUser = 1;
                }
                messageInfos.add(new MessageInfo(msg, isUser));
                messageAdapter.notifyDataSetChanged();
                messageRecyclerView.smoothScrollToPosition(messageInfos.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setMessageRecyclerView() {
        messageAdapter = new MessageAdapter(this, messageInfos);
        messageRecyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private Boolean checkGooglePlayServices() {
        Integer status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            Log.d("TAG", "Error");
            return false;
        } else {
            Log.d("TAG", "Google Play Services Updated!");
            return true;
        }
    }

    private void onClickBackBtn() {
        iv_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onClickSendBtn() {
        iv_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_bottom_comment.length() > 0) {
                    DatabaseReference messageRef = dbRef.child("messages").child("conversations").child(roomId).push();
                    setConversationDB(messageRef);
                    et_bottom_comment.setText("");
                    if (checkGooglePlayServices()) {

                    } else {
                        Log.d("TAG", "Device doesn't have google play services!");
                        Toast.makeText(ChatDetailActivity.this, "Device doesn't have google play services!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ChatDetailActivity.this, "Write your message!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setConversationDB(DatabaseReference msgRef) {
        Map<String, Object> deleteObj = new HashMap<>();
        deleteObj.put(myId, false);
        Map<String, Object> result = new HashMap<>();
        result.put("deleteForUser", deleteObj);
        result.put("deleted", 0);
        result.put("id", msgRef.getKey());
        result.put("message", et_bottom_comment.getText().toString());
        result.put("timestamp", ServerValue.TIMESTAMP);
        result.put("userId", myId);
        msgRef.updateChildren(result);
    }
}