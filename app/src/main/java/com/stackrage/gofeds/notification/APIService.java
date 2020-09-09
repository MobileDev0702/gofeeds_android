package com.stackrage.gofeds.notification;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_QjVv44:APA91bEpd8HIWReOMvyPT_vtT-hPB4P6nHJqjNmnAKGL-ZTDEH0L9ptEUQ8bVQzllbAhQiiaHQ3_EFdoCqO1xbdxP1v5TNXG2_qtvgMwwZ8n-vAHPJWIv__PI7PPUO8AjNoQremscAma"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body JsonObject body);
}
