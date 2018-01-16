package com.jerry.nurse.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jerry.nurse.R;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.util.BackUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

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

    private ProgressDialog mDialog;

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
        L.i("网址是:" + mUrl);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (title != null) {
            mTitleBar.setVisibility(View.VISIBLE);
            mTitleBar.setTitle(title);
        } else {
            mTitleBar.setVisibility(View.GONE);
        }

        mHistories.add(mUrl);

        // 清除WebView缓存
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
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

            public boolean shouldOverviewUrlLoading(WebView view, String url) {
                L.i("shouldOverviewUrlLoading");
                view.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                L.i("onPageStarted");
                showProgressDialog();
            }

            public void onPageFinished(WebView view, String url) {
                L.i("onPageFinished");
                closeProgressDialog();
            }

            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                L.i("onReceivedError");
                closeProgressDialog();
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
        public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            L.i(url);
            BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    new PermissionListener() {
                        @Override
                        public void onGranted() {
                            String name;
                            int start = url.indexOf("FX/");
                            int end = url.indexOf("split");
                            name = url.substring(start + 3, end);
                            try {
                                name = URLDecoder.decode(name, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            OkHttpUtils.get().url(url).build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(),
                                    name + ".xlsx") {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    L.i("下载失败" + e);
                                }

                                @Override
                                public void onResponse(File response, int id) {
                                    L.i("下载成功");
                                    T.showLong(HtmlActivity.this, "已保存到" + response.getAbsolutePath());
                                }
                            });
                        }

                        @Override
                        public void onDenied(List<String> deniedPermission) {

                        }
                    });
        }

    }

    private void showProgressDialog() {
        mProgressDialogManager.show();
    }

    private void closeProgressDialog() {
        mProgressDialogManager.dismiss();
    }
}
