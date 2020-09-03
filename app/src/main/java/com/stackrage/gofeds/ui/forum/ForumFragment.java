package com.stackrage.gofeds.ui.forum;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.stackrage.gofeds.AddQuestionActivity;
import com.stackrage.gofeds.LoadingIndicator;
import com.stackrage.gofeds.R;
import com.stackrage.gofeds.UserProfileActivity;
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

public class ForumFragment extends Fragment {

    private ImageView iv_add;
    private RecyclerView forumRecyclerView;
    private ForumAdapter forumAdapter;

    private ArrayList<Integer> avatarList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> questionList = new ArrayList<>();
    private ArrayList<String> answerList = new ArrayList<>();
    private ArrayList<String> quesIdList = new ArrayList<>();

    private LoadingIndicator loadingIndicator;

    @Override
    public void onStart() {
        super.onStart();
        removeData();
        loadData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_forum, container, false);
        loadingIndicator = LoadingIndicator.getInstance();

        setForumRecyclerView(root);
        onClickAddBtn(root);
        return root;
    }

    private void removeData() {
        avatarList.clear();
        nameList.clear();
        questionList.clear();
        answerList.clear();
    }

    private void loadData() {
        loadingIndicator.showProgress(getContext());
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<JsonObject> call = apiInterface.viewfaq();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String response_body = response.body().toString();

                try {
                    JSONObject respObject = new JSONObject(response_body);
                    Boolean isSuccess = respObject.getBoolean("success");
                    if (isSuccess) {
                        JSONArray dataArray = respObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataObject = dataArray.getJSONObject(i);
                            String username = dataObject.getString("username");
                            String question = dataObject.getString("question");
                            String answer = dataObject.getString("answer");
                            String quesid = dataObject.getString("question_id");
                            avatarList.add(R.drawable.user);
                            nameList.add(username);
                            questionList.add(question);
                            answerList.add(answer);
                            quesIdList.add(quesid);
                        }
                        forumAdapter.notifyDataSetChanged();
                    } else {
                        String msg = respObject.getString("message");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
                loadingIndicator.hideProgress();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                loadingIndicator.hideProgress();
            }
        });
    }

    private void setForumRecyclerView(View root) {
        forumRecyclerView = root.findViewById(R.id.forumRecyclerView);
        forumAdapter = new ForumAdapter(getContext(), avatarList, nameList, questionList, answerList, quesIdList);
        forumRecyclerView.setAdapter(forumAdapter);
        forumRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void onClickAddBtn(View root) {
        iv_add = root.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddQuestionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}