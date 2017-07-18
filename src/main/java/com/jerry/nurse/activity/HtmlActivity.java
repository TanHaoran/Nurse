package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jerry.nurse.R;
import com.jerry.nurse.view.TitleBar;

import butterknife.Bind;

public class HtmlActivity extends BaseActivity {

    public static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_URL = "extra_url";

    @Bind(R.id.tb_html)
    TitleBar mTitleBar;

    @Bind(R.id.wv_plugin)
    WebView mWebView;

    public static Intent getIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, HtmlActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    public static Intent getIntent(Context context, String url, int titleRes) {
        String title = context.getResources().getString(titleRes);
        return getIntent(context, url, title);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_plugin;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        String url = getIntent().getStringExtra(EXTRA_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);

        mTitleBar.setTitle(title);

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
