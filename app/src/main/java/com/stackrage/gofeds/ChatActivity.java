package com.stackrage.gofeds;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChatActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private ImageView iv_back_btn;

    private ArrayList<UserInfo> userInfos = new ArrayList<>();

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
        Toast.makeText(this, FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
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
                    String image = chatSnapshot.child("image").getValue().toString();

                    String timestamp = chatSnapshot.child("lastMessageTimeStamp").getValue().toString();
                    SimpleDateFormat sfd = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                    String date = sfd.format(Long.parseLong(timestamp));

                    UserInfo info = new UserInfo(image, receiverId, receiverUser, lastMsg, date, conversationId, Long.parseLong(timestamp));
                    userInfos.add(info);
                }
                Collections.sort(userInfos, new Comparator<UserInfo>() {

                    @Override
                    public int compare(UserInfo o1, UserInfo o2) {
                        return (int)(o2.getTimestamp()-o1.getTimestamp());
                    }
                });
                chatAdapter.notifyDataSetChanged();
                loadingIndicator.hideProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingIndicator.hideProgress();
            }
        });
    }

    private void setChatRecyclerView() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatAdapter = new ChatAdapter(this, userInfos);
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