package com.stackrage.gofeds.ui.feed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stackrage.gofeds.LoadingIndicator;
import com.stackrage.gofeds.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Struct;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class FeedFragment extends Fragment {

    public static final String TAG = "HttpRequest";

    private RecyclerView feedRecyclerView;
    private FeedAdapter feedAdapter;

    private ArrayList<String> photoList = new ArrayList<>();
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> timeList = new ArrayList<>();
    private ArrayList<String> urlList = new ArrayList<>();

    private JSONArray jsonArray = new JSONArray();

    private LoadingIndicator loadingIndicator;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_feed, container, false);

        loadingIndicator = LoadingIndicator.getInstance();
        loadingIndicator.showProgress(getContext());
        setFeedRecyclerView(root);
        loadNews();
        return root;
    }

    private void loadNews() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=ebbbb3f623174e94a6e4db85208fba55";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.get("status").toString().equals("ok")) {
                                jsonArray = jsonObject.getJSONArray("articles");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject child = jsonArray.getJSONObject(i);
                                        photoList.add(child.getString("urlToImage"));
                                        titleList.add(child.getString("title"));
                                        timeList.add(child.getString("publishedAt"));
                                        urlList.add(child.getString("url"));
                                        feedAdapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                loadingIndicator.hideProgress();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            loadingIndicator.hideProgress();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                loadingIndicator.hideProgress();
            }
        });
        queue.add(stringRequest);
    }

    private void setFeedRecyclerView(View root) {
        feedRecyclerView = root.findViewById(R.id.feedRecyclerView);
        feedAdapter = new FeedAdapter(getContext(), photoList, titleList, timeList, urlList);
        feedRecyclerView.setAdapter(feedAdapter);
        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }
}