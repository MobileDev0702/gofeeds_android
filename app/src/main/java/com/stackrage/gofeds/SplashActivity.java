package com.stackrage.gofeds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

public class SplashActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";
    public static final String PREF_BADGECOUNT = "PREFERENCE_BADGECOUNT";
    private static int splashInterval = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
//        if (getIntent().getExtras() != null) {
//            for (String key : getIntent().getExtras().keySet()) {
//                Object value = getIntent().getExtras().get(key);
//                Log.e("FirebaseDataReceiver", "Key: " + key + " Value: " + value);
//                if (key.equals("badge")) {
//                    SharedPreferences badgePref = getSharedPreferences(PREF_BADGECOUNT, Context.MODE_PRIVATE);
//                    badgePref.edit().putInt("BadgeCount", Integer.parseInt(value.toString())).commit();
//                }
//
//            }
//        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
                String id = idPref.getString("Id", "");
                if (id.isEmpty()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, splashInterval);
    }
}