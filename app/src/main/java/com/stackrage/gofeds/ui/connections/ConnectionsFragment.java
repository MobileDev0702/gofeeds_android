package com.stackrage.gofeds.ui.connections;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.stackrage.gofeds.ChatActivity;
import com.stackrage.gofeds.LoadingIndicator;
import com.stackrage.gofeds.R;
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

public class ConnectionsFragment extends Fragment {

    public static final String PREF_ID = "PREFERENCE_ID";

    private RecyclerView connectionRecyclerView;
    private ConnectionAdapter connectionAdapter;
    private TextView tv_exact_btn, tv_possible_btn;
    private View exact_underline, possible_underline;
    private ImageView iv_chat_btn;
    private ArrayList<Integer> avatarList = new ArrayList<>();
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> locationList = new ArrayList<>();

    private ApiInterface apiInterface;
    private LoadingIndicator loadingIndicator;

    @Override
    public void onStart() {
        super.onStart();
        removeData();
        getConnectMatch(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_connections, container, false);

        initComponent(root);
        setConnectionRecyclerView(root);
        onClickExactMatchBtn(root);
        onClickPossibleMatchBtn(root);
        onClickChatBtn(root);
        return root;
    }

    private void initComponent(View root) {
        iv_chat_btn = root.findViewById(R.id.iv_chat_btn);
        tv_exact_btn = root.findViewById(R.id.tv_exact_btn);
        exact_underline = root.findViewById(R.id.v_exact_underline);
        tv_possible_btn = root.findViewById(R.id.tv_possible_btn);
        possible_underline = root.findViewById(R.id.v_possible_underline);

        exact_underline.setVisibility(View.VISIBLE);
        possible_underline.setVisibility(View.INVISIBLE);
        loadingIndicator = LoadingIndicator.getInstance();
    }

    private void removeData() {
        avatarList.clear();
        idList.clear();
        nameList.clear();
        locationList.clear();
    }

    private void getConnectMatch(Boolean isExact) {
        loadingIndicator.showProgress(getContext());
        SharedPreferences idPref = getActivity().getSharedPreferences(PREF_ID, Context.MODE_PRIVATE);
        String id = idPref.getString("Id", "");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBody requestId = RequestBody.create(MediaType.parse("multipart/form-data"), id);
        Call<JsonObject> call;
        if (isExact) {
            call = apiInterface.exactmatch(requestId);
        } else {
            call = apiInterface.possiblematch(requestId);
        }

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
                            String userid = dataObject.getString("user_id");
                            String username = dataObject.getString("username");
                            String currentport = dataObject.getString("current_port");
                            avatarList.add(R.drawable.user);
                            idList.add(userid);
                            nameList.add(username);
                            locationList.add(currentport);
                        }
                        connectionAdapter.notifyDataSetChanged();
                    } else {
                        String msg = respObject.getString("message");
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
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

    private void onClickExactMatchBtn(View root) {
        tv_exact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exact_underline.setVisibility(View.VISIBLE);
                possible_underline.setVisibility(View.INVISIBLE);
                removeData();
                getConnectMatch(true);
            }
        });
    }

    private void onClickPossibleMatchBtn(View root) {
        tv_possible_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exact_underline.setVisibility(View.INVISIBLE);
                possible_underline.setVisibility(View.VISIBLE);
                removeData();
                getConnectMatch(false);
            }
        });
    }

    private void onClickChatBtn(View root) {
        iv_chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setConnectionRecyclerView(View root) {
        connectionRecyclerView = root.findViewById(R.id.connectionRecyclerView);
        connectionAdapter = new ConnectionAdapter(getContext(), avatarList, idList, nameList, locationList);
        connectionRecyclerView.setAdapter(connectionAdapter);
        connectionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }
}