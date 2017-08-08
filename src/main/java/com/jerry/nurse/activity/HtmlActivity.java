package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jerry.nurse.R;
import com.jerry.nurse.view.TitleBar;

import butterknife.Bind;

public class HtmlActivity extends BaseActivity {

    private static final String EXTRA_URL = "extra_url";
    private static final String EXTRA_TITLE = "extra_show_title";

    @Bind(R.id.tb_html)
    TitleBar mTitleBar;

    @Bind(R.id.wv_html)
    WebView mWebView;

    public static Intent getIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, HtmlActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_html;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        String url = getIntent().getStringExtra(EXTRA_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title != null) {
            mTitleBar.setVisibility(View.VISIBLE);
            mTitleBar.setTitle(title);
        } else {
            mTitleBar.setVisibility(View.GONE);
        }

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
