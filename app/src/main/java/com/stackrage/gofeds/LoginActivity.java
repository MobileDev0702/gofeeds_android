package com.stackrage.gofeds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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

public class LoginActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";
    public static final String PREF_USERNAME = "PREFERENCE_USERNAME";
    public static final String PREF_EMAIL = "PREFERENCE_EMAIL";
    public static final String PREF_RANK = "PREFERENCE_RANK";
    public static final String PREF_AGENCY = "PREFERENCE_AGENCY";
    public static final String PREF_OFFICE = "PREFERENCE_OFFICE";
    public static final String PREF_CURRENTPORT = "PREFERENCE_CURRENTPORT";
    public static final String PREF_DESIREPORT = "PREFERENCE_DESIREPORT";
    public static final String PREF_FTOKEN = "PREFERENCE_FTOKEN";

    private EditText et_username, et_pwd;
    private Switch sw_remember;
    private TextView tv_forgotpwd;
    private TextView tv_login_btn;
    private TextView tv_signup;

    LoadingIndicator loadingIndicator;
    private DatabaseReference dbRef;

    private String ftoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponent();
        initData();
        onClickLoginBtn();
        onClickSignupBtn();
    }

    private void initComponent() {
        et_username = findViewById(R.id.et_username);
        et_pwd = findViewById(R.id.et_pwd);
        sw_remember = findViewById(R.id.sw_remember);
        tv_forgotpwd = findViewById(R.id.tv_forgotpwd);
        tv_login_btn = findViewById(R.id.tv_login_btn);
        tv_signup = findViewById(R.id.tv_signup);
        loadingIndicator = LoadingIndicator.getInstance();
    }

    private void initData() {
        final SharedPreferences ftokenPref = getSharedPreferences(PREF_FTOKEN, Context.MODE_PRIVATE);
        ftoken = ftokenPref.getString("FToken", "");
        if (ftoken.isEmpty()) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    ftoken = task.getResult().getToken();
                }
            });
        }
    }

    private void onClickLoginBtn() {
        tv_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingIndicator.showProgress(LoginActivity.this);
                String username = et_username.getText().toString();
                String pwd = et_pwd.getText().toString();

                if (username.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please input email or username!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                if (pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please input password!", Toast.LENGTH_LONG).show();
                    loadingIndicator.hideProgress();
                    return;
                }
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                RequestBody requestUsername = RequestBody.create(MediaType.parse("multipart/form-data"), username);
                RequestBody requestPwd = RequestBody.create(MediaType.parse("multipart/form-data"), pwd);
                RequestBody requestFToken = RequestBody.create(MediaType.parse("multipart/form-data"), ftoken);
                RequestBody requestDeviceId = RequestBody.create(MediaType.parse("multipart/form-data"), "test");

                Call<JsonObject> call = apiInterface.login(requestUsername, requestPwd, requestFToken, requestDeviceId);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        String response_body = response.body().toString();

                        try {
                            JSONObject dataObject = new JSONObject(response_body);
                            Boolean isSuccess = dataObject.getBoolean("success");
                            String msg = dataObject.getString("message");
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                            if (isSuccess) {
                                String id = dataObject.getString("id");
                                SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
                                idPref.edit().putString("Id", id).commit();

                                String username = dataObject.getString("username");
                                SharedPreferences usernamePref = getSharedPreferences(PREF_USERNAME, Context.MODE_PRIVATE);
                                usernamePref.edit().putString("Username", username).commit();

                                String email = dataObject.getString("email");
                                SharedPreferences emailPref = getSharedPreferences(PREF_EMAIL, Context.MODE_PRIVATE);
                                emailPref.edit().putString("Email", email).commit();

                                String rank = dataObject.getString("rank");
                                SharedPreferences rankPref = getSharedPreferences(PREF_RANK, Context.MODE_PRIVATE);
                                rankPref.edit().putString("Rank", rank).commit();

                                String agency = dataObject.getString("agency");
                                SharedPreferences agencyPref = getSharedPreferences(PREF_AGENCY, Context.MODE_PRIVATE);
                                agencyPref.edit().putString("Agency", agency).commit();

//                                String office = dataObject.getString("office");
                                SharedPreferences officePref = getSharedPreferences(PREF_OFFICE, Context.MODE_PRIVATE);
                                officePref.edit().putString("Office", "").commit();

                                String currentport = dataObject.getString("current_port");
                                SharedPreferences currentportPref = getSharedPreferences(PREF_CURRENTPORT, Context.MODE_PRIVATE);
                                currentportPref.edit().putString("CurrentPort", currentport).commit();

                                String desireport = dataObject.getString("desire_port");
                                SharedPreferences desireportPref = getSharedPreferences(PREF_DESIREPORT, Context.MODE_PRIVATE);
                                desireportPref.edit().putString("DesirePort", desireport).commit();

                                String ftoken = dataObject.getString("ftoken");
                                SharedPreferences ftokenPref = getSharedPreferences(PREF_FTOKEN, Context.MODE_PRIVATE);
                                ftokenPref.edit().putString("FToken", ftoken).commit();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingIndicator.hideProgress();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        loadingIndicator.hideProgress();
                    }
                });
            }
        });
    }

    private void onClickSignupBtn() {
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}