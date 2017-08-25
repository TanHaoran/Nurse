package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jerry.nurse.R;
import com.jerry.nurse.util.PictureUtil;

import butterknife.Bind;
import butterknife.OnClick;

public class PhotoActivity extends BaseActivity {

    private static final String EXTRA_LOCAL_PATH = "extra_local_path";
    private static final String EXTRA_REMOTE_PATH = "extra_remote_path";

    @Bind(R.id.iv_photo)
    ImageView mPhotoImageView;


    public static Intent getIntent(Context context, String localPath, String remotePath) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(EXTRA_LOCAL_PATH, localPath);
        intent.putExtra(EXTRA_REMOTE_PATH, remotePath);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_photo;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String localPath = getIntent().getStringExtra(EXTRA_LOCAL_PATH);
        String remotePath = getIntent().getStringExtra(EXTRA_REMOTE_PATH);
        if (!TextUtils.isEmpty(localPath)) {
            Bitmap bitmap = PictureUtil.getScaleBitmap(localPath, this);
            mPhotoImageView.setImageBitmap(bitmap);
        } else {
            Glide.with(this).load(remotePath)
                    .placeholder(R.drawable.icon_avatar_default).into(mPhotoImageView);
        }
    }

    @OnClick(R.id.ll_photo)
    void onPhoto(View view) {
        finish();
    }
}
