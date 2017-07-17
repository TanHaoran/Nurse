package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jerry.nurse.R;

import butterknife.Bind;

public class PluginActivity extends BaseActivity {

    private static final String EXTRA_URL = "extraUrl";

    @Bind(R.id.wv_plugin)
    WebView mWebView;

    public static Intent getIntent(Context context, String url) {
        Intent intent = new Intent(context, PluginActivity.class);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_plugin;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        String url = getIntent().getStringExtra(EXTRA_URL);

        mWebView.getSettings().setJavaScriptEnabled(true);
        // 打开DOM储存API
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        mWebView.loadUrl(url);
    }
}
