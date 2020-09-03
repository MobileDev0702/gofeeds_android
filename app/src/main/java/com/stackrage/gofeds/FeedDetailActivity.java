package com.stackrage.gofeds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FeedDetailActivity extends AppCompatActivity {

    LoadingIndicator loadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        loadingIndicator = LoadingIndicator.getInstance();
        loadingIndicator.showProgress(this);
        String url = getIntent().getStringExtra("Url");
        WebView detailView = findViewById(R.id.wv_detailview);

        detailView.getSettings().setLoadsImagesAutomatically(true);
        detailView.getSettings().setJavaScriptEnabled(true);
        detailView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        detailView.loadUrl(url);

        detailView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                loadingIndicator.hideProgress();
            }
        });
    }
}