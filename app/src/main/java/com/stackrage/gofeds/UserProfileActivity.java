package com.stackrage.gofeds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.stackrage.gofeds.api.ApiClient;
import com.stackrage.gofeds.api.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";
    public static final String PREF_USERNAME = "PREFERENCE_USERNAME";

    private ImageView iv_back_btn;
    private TextView tv_username;
    private ImageView iv_avatar;
    private TextView tv_rank, tv_agency, tv_office, tv_currentport, tv_desiredport;
    private TextView tv_start_btn;

    private LoadingIndicator loadingIndicator;
    private DatabaseReference dbRef;
    private String myId, userId, roomId, myname, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initComponent();
        loadData();
        onClickBackBtn();
        onClickStartChatBtn();
    }

    private void initComponent() {
        iv_back_btn = findViewById(R.id.iv_back_btn);
        tv_username = findViewById(R.id.tv_username);
        iv_avatar = findViewById(R.id.iv_avatar);
        tv_rank = findViewById(R.id.tv_rank);
        tv_agency = findViewById(R.id.tv_agency);
        tv_office = findViewById(R.id.tv_office);
        tv_currentport = findViewById(R.id.tv_currentport);
        tv_desiredport = findViewById(R.id.tv_desiredport);
        tv_start_btn = findViewById(R.id.tv_start_btn);
        loadingIndicator = LoadingIndicator.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();
        SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
        myId = idPref.getString("Id", "");
        SharedPreferences usernamePref = getSharedPreferences(PREF_USERNAME, Context.MODE_PRIVATE);
        myname = usernamePref.getString("Username", "");
    }

    private void loadData() {
        userId = getIntent().getStringExtra("Id");
        loadingIndicator.showProgress(this);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestId = RequestBody.create(MediaType.parse("multipart/form-data"), userId);

        Call<JsonObject> call = apiInterface.myprofile(requestId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String response_body = response.body().toString();

                try {
                    JSONObject dataObject = new JSONObject(response_body);
                    Boolean isSuccess = dataObject.getBoolean("success");
                    if (isSuccess) {
                        username = dataObject.getString("username");
                        String rank = dataObject.getString("rank");
                        String agency = dataObject.getString("agency");
                        String office = dataObject.getString("office");
                        String currentport = dataObject.getString("current_port");
                        String desireport = dataObject.getString("desire_port");
                        tv_username.setText(username);
                        tv_rank.setTextColor(Color.BLACK);
                        tv_rank.setText(rank);
                        tv_agency.setTextColor(Color.BLACK);
                        tv_agency.setText(agency);
                        tv_office.setTextColor(Color.BLACK);
                        tv_office.setText(office);
                        tv_currentport.setTextColor(Color.BLACK);
                        tv_currentport.setText(currentport);
                        tv_desiredport.setTextColor(Color.BLACK);
                        tv_desiredport.setText(desireport);
                    } else {
                        String msg = dataObject.getString("message");
                        Toast.makeText(UserProfileActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                loadingIndicator.hideProgress();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                loadingIndicator.hideProgress();
            }
        });
    }

    private void onClickBackBtn() {
        iv_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onClickStartChatBtn() {
        tv_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef.child("messages").child("chatUsers").child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer roomCnt = 0;
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String receiverId = dataSnapshot.child("receiverId").getValue().toString();
                                String senderId = dataSnapshot.child("senderId").getValue().toString();
                                if ((senderId.equals(userId) && receiverId.equals(myId)) || (senderId.equals(senderId) && receiverId.equals(userId))) {
                                    roomId = dataSnapshot.child("conversationId").getValue().toString();
                                } else {
                                    roomCnt ++;
                                }
                            }
                            if ((int)(snapshot.getChildrenCount()) == roomCnt) {
                                DatabaseReference roomRef = dbRef.child("messages").child("chatUsers").child(myId).push();
                                roomId = roomRef.getKey();
                                initChatUserDB(roomRef);
                            }
                        } else {
                            DatabaseReference roomRef = dbRef.child("messages").child("chatUsers").child(myId).push();
                            roomId = roomRef.getKey();
                            initChatUserDB(roomRef);
                        }
                        Intent intent = new Intent(UserProfileActivity.this, ChatDetailActivity.class);
                        intent.putExtra("RoomId", roomId);
                        intent.putExtra("ReceiverId", userId);
                        intent.putExtra("ReceiverUser", username);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void initChatUserDB(DatabaseReference roomRef) {
        Map<String, Object> chatUserResult = new HashMap<>();
        chatUserResult.put("chatDeletedForUser", 0);
        chatUserResult.put("conversationId", roomRef.getKey());
        chatUserResult.put("creatorId", myId);
        chatUserResult.put("creatorUser", myname);
        chatUserResult.put("deleted", 0);
        chatUserResult.put("isRead", 1);
        chatUserResult.put("lastMessage", "");
        chatUserResult.put("lastMessageTimeStamp", ServerValue.TIMESTAMP);
        chatUserResult.put("messageId", "");
        chatUserResult.put("otherConversationId", "");
        chatUserResult.put("receiverId", userId);
        chatUserResult.put("receiverUser", username);
        chatUserResult.put("senderId", myId);
        chatUserResult.put("timestamp", ServerValue.TIMESTAMP);
        roomRef.updateChildren(chatUserResult);

        Map<String, Object> chatOppoResult = new HashMap<>();
        chatOppoResult.put("chatDeletedForUser", 0);
        chatOppoResult.put("conversationId", roomRef.getKey());
        chatOppoResult.put("creatorId", myId);
        chatOppoResult.put("creatorUser", myname);
        chatOppoResult.put("deleted", 0);
        chatOppoResult.put("isRead", 1);
        chatOppoResult.put("lastMessage", "");
        chatOppoResult.put("lastMessageTimeStamp", ServerValue.TIMESTAMP);
        chatOppoResult.put("messageId", "");
        chatOppoResult.put("otherConversationId", "");
        chatOppoResult.put("receiverId", myId);
        chatOppoResult.put("receiverUser", myname);
        chatOppoResult.put("senderId", userId);
        chatOppoResult.put("timestamp", ServerValue.TIMESTAMP);
        dbRef.child("messages").child("chatUsers").child(userId).child(roomRef.getKey()).updateChildren(chatOppoResult);
    }
}