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
import com.google.gson.JsonObject;
import com.stackrage.gofeds.api.ApiClient;
import com.stackrage.gofeds.api.ApiInterface;
import com.stackrage.gofeds.notification.APIService;
import com.stackrage.gofeds.notification.Client;
import com.stackrage.gofeds.notification.MyResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatDetailActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";
    public static final String PREF_BADGECOUNT = "PREFERENCE_BADGECOUNT";
    public static final String PREF_IMAGE = "PREFERENCE_IMAGE";

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
    private String ftoken, deviceId, image;

    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        initComponent();
        initData();
        loadToken();
        loadMessage();
        setMessageRecyclerView();
        onClickBackBtn();
        onClickSendBtn();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
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

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestId = RequestBody.create(MediaType.parse("multipart/form-data"), receiverId);
        RequestBody requestReset = RequestBody.create(MediaType.parse("multipart/form-data"), "true");

        Call<JsonObject> call = apiInterface.updatebadge(requestId, requestReset);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String response_body = response.body().toString();

                try {
                    JSONObject dataObject = new JSONObject(response_body);
                    Boolean isSuccess = dataObject.getBoolean("success");
                    if (isSuccess) {
                        SharedPreferences badgePref = getSharedPreferences(PREF_BADGECOUNT, Context.MODE_PRIVATE);
                        badgePref.edit().putInt("BadgeCount", 0);
                    } else {
                        String msg = dataObject.getString("message");
                        Toast.makeText(ChatDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadToken() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestId = RequestBody.create(MediaType.parse("multipart/form-data"), receiverId);

        Call<JsonObject> call = apiInterface.myprofile(requestId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String response_body = response.body().toString();

                try {
                    JSONObject dataObject = new JSONObject(response_body);
                    Boolean isSuccess = dataObject.getBoolean("success");
                    if (isSuccess) {
                        ftoken = dataObject.getString("ftoken");
                        deviceId = dataObject.getString("device_id");
                        image = dataObject.getString("image");
                    } else {
                        String msg = dataObject.getString("message");
                        Toast.makeText(ChatDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

                String avatar = "";
                if (myId.equals(senderId)) {
                    isUser = 0;
                    SharedPreferences imagePref = getSharedPreferences(PREF_IMAGE, Context.MODE_PRIVATE);
                    avatar = imagePref.getString("Image", "");
                } else {
                    isUser = 1;
                    avatar = image;
                }
                messageInfos.add(new MessageInfo(msg, isUser, avatar));
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
                    if (checkGooglePlayServices()) {

                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        RequestBody requestId = RequestBody.create(MediaType.parse("multipart/form-data"), receiverId);
                        RequestBody requestReset = RequestBody.create(MediaType.parse("multipart/form-data"), "false");

                        Call<JsonObject> call = apiInterface.updatebadge(requestId, requestReset);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                String response_body = response.body().toString();

                                try {
                                    JSONObject dataObject = new JSONObject(response_body);
                                    Boolean isSuccess = dataObject.getBoolean("success");
                                    if (isSuccess) {
                                        Integer badgeCount = dataObject.getInt("badgeCount");
                                        JsonObject jsonObject = new JsonObject();
                                        jsonObject.addProperty("to", ftoken);
                                        jsonObject.addProperty("priority", "high");
                                        jsonObject.addProperty("message", et_bottom_comment.getText().toString());
                                        jsonObject.addProperty("mSender_id", "123");
                                        jsonObject.addProperty("sound", "enabled");

                                        if (deviceId.equals("iPhone")) {
                                            JsonObject notiObj = new JsonObject();
                                            notiObj.addProperty("body", et_bottom_comment.getText().toString());
                                            notiObj.addProperty("badge", badgeCount);
                                            notiObj.addProperty("mSender_id", "123");
                                            notiObj.addProperty("sound", "default");
                                            notiObj.addProperty("title", "You have a new message");

                                            jsonObject.add("notification", notiObj);
                                        }

                                        JsonObject dataObj = new JsonObject();
                                        dataObj.addProperty("mSender_id", myId);
                                        dataObj.addProperty("mReciver_id", receiverId);
                                        dataObj.addProperty("badge", badgeCount);
                                        dataObj.addProperty("roomId", roomId);
                                        dataObj.addProperty("receiverUser", receiverUser);
                                        dataObj.addProperty("body", et_bottom_comment.getText().toString());
                                        dataObj.addProperty("title", "You have a new message");

                                        jsonObject.add("data", dataObj);

                                        apiService.sendNotification(jsonObject).enqueue(new Callback<MyResponse>() {
                                            @Override
                                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                if (response.code() == 200) {
                                                    if (response.body().success != 1) {
                                                        Toast.makeText(ChatDetailActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        et_bottom_comment.setText("");
                                                    }
                                                } else {
                                                    et_bottom_comment.setText("");
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                                Toast.makeText(ChatDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                et_bottom_comment.setText("");
                                            }
                                        });
                                    } else {
                                        String msg = dataObject.getString("message");
                                        Toast.makeText(ChatDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                                        et_bottom_comment.setText("");
                                    }
                                } catch(JSONException e) {
                                    e.printStackTrace();
                                    et_bottom_comment.setText("");
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Toast.makeText(ChatDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                et_bottom_comment.setText("");
                            }
                        });
                    } else {
                        Log.d("TAG", "Device doesn't have google play services!");
                        Toast.makeText(ChatDetailActivity.this, "Device doesn't have google play services!", Toast.LENGTH_LONG).show();
                        et_bottom_comment.setText("");
                    }
                } else {
                    Toast.makeText(ChatDetailActivity.this, "Write your message!", Toast.LENGTH_SHORT).show();
                    et_bottom_comment.setText("");
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