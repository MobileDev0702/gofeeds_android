package com.stackrage.gofeds;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.stackrage.gofeds.api.ApiClient;
import com.stackrage.gofeds.api.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswerListActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";

    private RecyclerView answerRecyclerView;
    private AnswerAdapter answerAdapter;
    private ImageView iv_back_btn;
    private TextView tv_question;
    private TextView tv_writeanswer;
    private EditText et_answer;

    private ArrayList<String> avatarList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> answerList = new ArrayList<>();
    private ArrayList<Integer> voteList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> answerIdList = new ArrayList<>();

    private LoadingIndicator loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_list);

        initData();
        setAnswerRecyclerView();
        loadData();
        onClickBackBtn();
        onClickWriteAnswerBtn();
    }

    private void removeData() {
        avatarList.clear();
        nameList.clear();
        answerList.clear();
        voteList.clear();
        idList.clear();
        answerIdList.clear();
    }

    private void initData() {
        tv_question = findViewById(R.id.tv_question);
        loadingIndicator = LoadingIndicator.getInstance();
        loadingIndicator.showProgress(this);
    }

    private void setAnswerRecyclerView() {
        answerRecyclerView = findViewById(R.id.answerRecyclerView);
        answerAdapter = new AnswerAdapter(this, avatarList, nameList, answerList, voteList, idList);
        answerRecyclerView.setAdapter(answerAdapter);
        answerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void loadData() {
        String quesId = getIntent().getStringExtra("Question_Id");
        String questionText = getIntent().getStringExtra("QuestionText");
        tv_question.setText(questionText);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestQuesId = RequestBody.create(MediaType.parse("multipart/form-data"), quesId);

        Call<JsonObject> call = apiInterface.viewallanswerofquestion(requestQuesId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String response_body = response.body().toString();

                try {
                    JSONObject respObject = new JSONObject(response_body);
                    Boolean isSuccess = respObject.getBoolean("success");
                    if (isSuccess) {
                        JSONArray dataArray = respObject.getJSONArray("answers");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObject = dataArray.getJSONObject(i);
                            String username = dataObject.getString("username");
                            String answer = dataObject.getString("answer");
                            Integer vote = dataObject.getInt("vote");
                            String id = dataObject.getString("user_id");
                            String answerid = dataObject.getString("answer_id");
                            String image = dataObject.getString("image");
                            String imageUrl = "";
                            if (image.isEmpty()) {
                                imageUrl = "http://stackrage.com/gofeeds/images/user1.png";
                            } else {
                                imageUrl = "http://stackrage.com/gofeeds/images/" + image;
                            }
                            avatarList.add(imageUrl);
                            nameList.add(username);
                            answerList.add(answer);
                            voteList.add(vote);
                            idList.add(id);
                            answerIdList.add(answerid);
                        }
                        answerAdapter.notifyDataSetChanged();
                    } else {
                        String msg = respObject.getString("message");
                        Toast.makeText(AnswerListActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                loadingIndicator.hideProgress();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AnswerListActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                loadingIndicator.hideProgress();
            }
        });
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

    private void onClickWriteAnswerBtn() {
        tv_writeanswer = findViewById(R.id.tv_writeanswer);
        tv_writeanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(AnswerListActivity.this);
                final View submitDialogView = factory.inflate(R.layout.submitlayout, null);
                final AlertDialog submitDialog = new AlertDialog.Builder(AnswerListActivity.this).create();
                submitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                submitDialog.setView(submitDialogView);

                submitDialogView.findViewById(R.id.tv_submit_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_answer = submitDialogView.findViewById(R.id.et_submit);
                        if (et_answer.getText().toString().isEmpty()) {
                            Toast.makeText(AnswerListActivity.this, "Please enter answer", Toast.LENGTH_LONG).show();
                        } else {
                            loadingIndicator.showProgress(AnswerListActivity.this);
                            String quesId = getIntent().getStringExtra("Question_Id");
                            SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
                            String id = idPref.getString("Id", "");
                            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                            RequestBody requestQuesId = RequestBody.create(MediaType.parse("multipart/form-data"), quesId);
                            RequestBody requestId = RequestBody.create(MediaType.parse("multipart/form-data"), id);
                            RequestBody requestAnswer = RequestBody.create(MediaType.parse("multipart/form-data"), et_answer.getText().toString());
                            RequestBody requestVote = RequestBody.create(MediaType.parse("multipart/form-data"), "0");

                            Call<JsonObject> call = apiInterface.submitfaqanswer(requestQuesId, requestId, requestAnswer, requestVote);
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    String response_body = response.body().toString();

                                    try {
                                        JSONObject dataObject = new JSONObject(response_body);
                                        Boolean isSuccess = dataObject.getBoolean("success");
                                        String msg = dataObject.getString("message");
                                        Toast.makeText(AnswerListActivity.this, msg, Toast.LENGTH_LONG).show();
                                        if (isSuccess) {
                                            submitDialog.dismiss();
                                            removeData();
                                            loadData();
                                        }
                                    } catch(JSONException e) {
                                        e.printStackTrace();
                                    }
                                    loadingIndicator.hideProgress();
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Toast.makeText(AnswerListActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    loadingIndicator.hideProgress();
                                }
                            });
                        }
                    }
                });
                submitDialogView.findViewById(R.id.tv_close_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitDialog.dismiss();
                    }
                });
                submitDialog.show();
            }
        });
    }

    public void onClickUpVote(final Integer pos) {
        String quesId = getIntent().getStringExtra("Question_Id");
        SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
        String id = idPref.getString("Id", "");
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestAnsId = RequestBody.create(MediaType.parse("multipart/form-data"), answerIdList.get(pos));
        RequestBody requestVote = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(voteList.get(pos) + 1));
        RequestBody requestQuesId = RequestBody.create(MediaType.parse("multipart/form-data"), quesId);
        RequestBody requestUserId = RequestBody.create(MediaType.parse("multipart/form-data"), id);

        Call<JsonObject> call = apiInterface.updatevote(requestAnsId, requestVote, requestQuesId, requestUserId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String response_body = response.body().toString();

                try {
                    JSONObject respObject = new JSONObject(response_body);
                    Boolean isSuccess = respObject.getBoolean("success");
                    if (isSuccess) {
                        JSONArray dataArray = respObject.getJSONArray("answers");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObject = dataArray.getJSONObject(i);
                            String answerid = dataObject.getString("answer_id");
                            if (answerIdList.get(pos).equals(answerid)) {
                                Integer vote = dataObject.getInt("vote");
                                voteList.set(pos, vote);
                            }
                        }
                        answerAdapter.notifyDataSetChanged();
                    } else {
                        String msg = respObject.getString("message");
                        Toast.makeText(AnswerListActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(AnswerListActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClickDownVote(final Integer pos) {
        if (voteList.get(pos) > 0) {
            String quesId = getIntent().getStringExtra("Question_Id");
            SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
            String id = idPref.getString("Id", "");
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            RequestBody requestAnsId = RequestBody.create(MediaType.parse("multipart/form-data"), answerIdList.get(pos));
            RequestBody requestVote = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(voteList.get(pos) - 1));
            RequestBody requestQuesId = RequestBody.create(MediaType.parse("multipart/form-data"), quesId);
            RequestBody requestUserId = RequestBody.create(MediaType.parse("multipart/form-data"), id);

            Call<JsonObject> call = apiInterface.updatevote(requestAnsId, requestVote, requestQuesId, requestUserId);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    String response_body = response.body().toString();

                    try {
                        JSONObject respObject = new JSONObject(response_body);
                        Boolean isSuccess = respObject.getBoolean("success");
                        if (isSuccess) {
                            JSONArray dataArray = respObject.getJSONArray("answers");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject dataObject = dataArray.getJSONObject(i);
                                String answerid = dataObject.getString("answer_id");
                                if (answerIdList.get(pos).equals(answerid)) {
                                    Integer vote = dataObject.getInt("vote");
                                    voteList.set(pos, vote);
                                }
                            }
                            answerAdapter.notifyDataSetChanged();
                        } else {
                            String msg = respObject.getString("message");
                            Toast.makeText(AnswerListActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(AnswerListActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}