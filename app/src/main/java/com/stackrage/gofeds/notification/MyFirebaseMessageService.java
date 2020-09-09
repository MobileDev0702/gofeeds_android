package com.stackrage.gofeds.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.stackrage.gofeds.ChatDetailActivity;
import com.stackrage.gofeds.MainActivity;
import com.stackrage.gofeds.R;

import java.util.Map;

public class MyFirebaseMessageService extends FirebaseMessagingService {

    public static final String PREF_BADGECOUNT = "PREFERENCE_BADGECOUNT";
    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String body = "", title = "";
        Integer badgeCount = 0;
        RemoteMessage.Notification noti = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        title = data.get("title");
        body = data.get("body");
        badgeCount = Integer.parseInt(data.get("badge"));
        SharedPreferences badgePref = getSharedPreferences(PREF_BADGECOUNT, Context.MODE_PRIVATE);
        badgePref.edit().putInt("BadgeCount", badgeCount).commit();
        String receiverId = data.get("mReciver_id");
        String receiverUser = data.get("receiverUser");
        String roomId = data.get("roomId");

        Intent intent = new Intent(this, ChatDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("ReceiverId", receiverId);
        intent.putExtra("RoomId", roomId);
        intent.putExtra("ReceiverUser", receiverUser);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setNumber(badgeCount)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder.setChannelId(channelId);
            channel.setShowBadge(true);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}
