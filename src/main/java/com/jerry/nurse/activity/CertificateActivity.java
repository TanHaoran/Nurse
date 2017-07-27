package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.jerry.nurse.R;
import com.jerry.nurse.listener.PhotoSelectListener;
import com.jerry.nurse.view.TitleBar;

import butterknife.Bind;


public class CertificateActivity extends BaseActivity {

    public static final String EXTRA_TITLE = "extra_title";

    @Bind(R.id.tb_certificate)
    TitleBar mTitleBar;

    @Bind(R.id.iv_certificate)
    ImageView mCertificateImageView;

    public static Intent getIntent(Context context, String title) {
        Intent intent = new Intent(context, CertificateActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    public static Intent getIntent(Context context, int titleRes) {
        String title = context.getResources().getString(titleRes);
        return getIntent(context, title);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_certificate;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        mTitleBar.setTitle(title);


        setPhotoSelectListener(mCertificateImageView, new PhotoSelectListener() {
            @Override
            public void onPhotoSelected(Bitmap bitmap) {
                mCertificateImageView.setImageBitmap(bitmap);
            }
        });

//        String url = "http://www.csdn.net/";
//        OkHttpUtils
//                .get()
//                .url(url)
//                .addParams("username", "hyman")
//                .addParams("password", "123")
//                .build()
//                .execute(new StringCallback()
//                {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        mTitleBar.setTitle("Oh yeah.");
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        mTitleBar.setTitle("Oh yeah.");
//                    }
//                });
    }

}
