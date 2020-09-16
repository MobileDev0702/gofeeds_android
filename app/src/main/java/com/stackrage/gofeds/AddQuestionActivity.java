package com.stackrage.gofeds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.stackrage.gofeds.api.ApiClient;
import com.stackrage.gofeds.api.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddQuestionActivity extends AppCompatActivity {

    public static final String PREF_ID = "PREFERENCE_ID";

    private TextView tv_close, tv_submit;
    private EditText et_question;

    private LoadingIndicator loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        onClickCloseBtn();
        onClickSubmitBtn();
    }

    private void onClickCloseBtn() {
        tv_close = findViewById(R.id.tv_close);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onClickSubmitBtn() {
        loadingIndicator = LoadingIndicator.getInstance();
        et_question = findViewById(R.id.et_add_question);
        tv_submit = findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = et_question.getText().toString();
                String replacedText = question.replace("'", "\\'");
                if (replacedText.isEmpty()) {
                    Toast.makeText(AddQuestionActivity.this, "Please enter your question", Toast.LENGTH_LONG).show();
                } else {
                    loadingIndicator.showProgress(AddQuestionActivity.this);
                    SharedPreferences idPref = getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
                    String id = idPref.getString("Id", "");
                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    RequestBody requestId = RequestBody.create(MediaType.parse("multipart/form-data"), id);
                    RequestBody requestQues = RequestBody.create(MediaType.parse("multipart/form-data"), replacedText);

                    Call<JsonObject> call = apiInterface.addfaq(requestId, requestQues);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            String response_body = response.body().toString();

                            try {
                                JSONObject dataObject = new JSONObject(response_body);
                                Boolean isSuccess = dataObject.getBoolean("success");
                                String msg = dataObject.getString("message");
                                Toast.makeText(AddQuestionActivity.this, msg, Toast.LENGTH_LONG).show();
                                if (isSuccess) {
                                    finish();
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                            loadingIndicator.hideProgress();
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(AddQuestionActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            loadingIndicator.hideProgress();
                        }
                    });
                }
            }
        });
    }
}