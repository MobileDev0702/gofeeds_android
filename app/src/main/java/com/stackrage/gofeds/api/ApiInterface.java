package com.stackrage.gofeds.api;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @Multipart
    @POST(EndApi.register)
    Call<JsonObject> register(@Part("email") RequestBody email,
                                @Part("username") RequestBody username,
                                @Part("firstname") RequestBody fname,
                                @Part("lastname") RequestBody lname,
                                @Part("password") RequestBody pwd,
                                @Part("rank") RequestBody rank,
                                @Part("agency") RequestBody agency,
                                @Part("current_port") RequestBody currentport,
                                @Part("desire_port") RequestBody desireport,
                                @Part("office") RequestBody office,
                                @Part("ftoken") RequestBody ftoken,
                                @Part("device_id") RequestBody deviceid);

    @Multipart
    @POST(EndApi.login)
    Call<JsonObject> login(@Part("username") RequestBody username,
                           @Part("password") RequestBody pwd,
                           @Part("ftoken") RequestBody ftoken,
                           @Part("device_id") RequestBody deviceid);

    @Multipart
    @POST(EndApi.myprofile)
    Call<JsonObject> myprofile(@Part("user_id") RequestBody userid);

    @Multipart
    @POST(EndApi.updateprofile)
    Call<JsonObject> updateprofile(@Part("id") RequestBody id,
                                   @Part("firstname") RequestBody fname,
                                   @Part("lastname") RequestBody lname,
                                   @Part("image") RequestBody image, //@Part MultipartBody.Part image,
                                   @Part("rank") RequestBody rank,
                                   @Part("agency") RequestBody agency,
                                   @Part("current_port") RequestBody currentport,
                                   @Part("desire_port") RequestBody desireport,
                                   @Part("office") RequestBody office);

    @Multipart
    @POST(EndApi.updatevote)
    Call<JsonObject> updatevote(@Part("id") RequestBody id,
                                @Part("vote") RequestBody vote,
                                @Part("question_id") RequestBody questionid,
                                @Part("user_id") RequestBody userid);

    @Multipart
    @POST(EndApi.updatebadge)
    Call<JsonObject> updatebadge(@Part("id") RequestBody id,
                                 @Part("reset") RequestBody reset);

    @Multipart
    @POST(EndApi.addfaq)
    Call<JsonObject> addfaq(@Part("user_id") RequestBody userid,
                            @Part("question") RequestBody question);

    @POST(EndApi.viewfaq)
    Call<JsonObject> viewfaq();

    @Multipart
    @POST(EndApi.viewusersbyagency)
    Call<JsonObject> viewusersbyagency();

    @Multipart
    @POST(EndApi.exactmatch)
    Call<JsonObject> exactmatch(@Part("user_id") RequestBody userid);

    @Multipart
    @POST(EndApi.possiblematch)
    Call<JsonObject> possiblematch(@Part("user_id") RequestBody userid);

    @Multipart
    @POST(EndApi.submitfaqanswer)
    Call<JsonObject> submitfaqanswer(@Part("question_id") RequestBody questionid,
                                     @Part("user_id") RequestBody userid,
                                     @Part("answer") RequestBody answer,
                                     @Part("vote") RequestBody vote);

    @Multipart
    @POST(EndApi.viewallsubmitedfaqanswer)
    Call<JsonObject> viewallsubmitedfaqanswer();

    @Multipart
    @POST(EndApi.viewallanswerofquestion)
    Call<JsonObject> viewallanswerofquestion(@Part("question_id") RequestBody questionid);
}
