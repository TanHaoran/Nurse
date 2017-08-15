package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jerry.nurse.R;
import com.jerry.nurse.util.BackUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.view.TitleBar;

import java.util.ArrayList;

import butterknife.Bind;

public class HtmlActivity extends BaseActivity {

    private static final String EXTRA_URL = "extra_url";
    private static final String EXTRA_TITLE = "extra_show_title";

    @Bind(R.id.tb_html)
    TitleBar mTitleBar;

    @Bind(R.id.wv_html)
    WebView mWebView;


    private String mUrl;
    private ArrayList<String> mHistories = new ArrayList<>();


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

        mUrl = getIntent().getStringExtra(EXTRA_URL);
        L.i("页面网址是：" + mUrl);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title != null) {
            mTitleBar.setVisibility(View.VISIBLE);
            mTitleBar.setTitle(title);
        } else {
            mTitleBar.setVisibility(View.GONE);
        }

        mHistories.add(mUrl);

        // 允许JS脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 设置字体所放大小不随系统改变
        mWebView.getSettings().setTextZoom(100);
        // 允许JS弹窗
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 打开DOM储存API
        mWebView.getSettings().setDomStorageEnabled(true);

        String t = mWebView.getTitle();
        String u = mWebView.getUrl();
        L.i("标题是：" + t);
        L.i("网址是：" + u);

        mWebView.addJavascriptInterface(new BackUtil(), "backUtil");

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        mWebView.loadUrl(mUrl);
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());

    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            if (mWebView.getUrl().equals(mUrl)) {
                super.onBackPressed();
            } else {
                mWebView.goBack();
            }
        } else {
            super.onBackPressed();
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }


}
