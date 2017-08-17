package com.jerry.nurse.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
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

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

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

        mWebView.addJavascriptInterface(new BackUtil(), "backUtil");

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        mWebView.loadUrl(mUrl);
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());

        mWebView.setWebChromeClient(new WebChromeClient() {
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });

    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
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
