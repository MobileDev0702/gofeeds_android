package com.stackrage.gofeds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private ImageView iv_back_btn;
    private ArrayList<Integer> photoList = new ArrayList<>();
    private ArrayList<String> receiverIdList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> lastchatList = new ArrayList<>();
    private ArrayList<String> timeList = new ArrayList<>();
    private ArrayList<String> roomId = new ArrayList<>();

    private DatabaseReference dbRef;
    private LoadingIndicator loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initFirebase();
        setChatRecyclerView();
        loadData();
        onClickBackBtn();
    }

    private void initFirebase() {
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    private void loadData() {
        loadingIndicator = LoadingIndicator.getInstance();
        loadingIndicator.showProgress(this);
        SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
        String id = idPref.getString("Id", "");
        dbRef.child("messages").child("chatUsers").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    String receiverId = chatSnapshot.child("receiverId").getValue().toString();
                    String receiverUser = chatSnapshot.child("receiverUser").getValue().toString();
                    String lastMsg = chatSnapshot.child("lastMessage").getValue().toString();
                    String conversationId = chatSnapshot.child("conversationId").getValue().toString();

                    String timestamp = chatSnapshot.child("lastMessageTimeStamp").getValue().toString();
                    SimpleDateFormat sfd = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                    String date = sfd.format(Long.parseLong(timestamp));
                    photoList.add(R.drawable.user);
                    receiverIdList.add(receiverId);
                    nameList.add(receiverUser);
                    lastchatList.add(lastMsg);
                    timeList.add(date);
                    roomId.add(conversationId);
                }
                chatAdapter.notifyDataSetChanged();
                loadingIndicator.hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setChatRecyclerView() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatAdapter = new ChatAdapter(this, photoList, receiverIdList, nameList, lastchatList, timeList, roomId);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void onClickBackBtn() {
        iv_back_btn = findViewById(R.id.iv_back_btn);
        iv_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}