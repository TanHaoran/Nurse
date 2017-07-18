package com.jerry.nurse.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.util.ActivityCollector;

import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_setting;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @OnClick(R.id.rl_change_password)
    void onChangePassword(View view) {
        Intent intent = ChangePasswordActivity.getIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.rl_help)
    void onHelp(View view) {
        Intent intent = HtmlActivity.getIntent(this, "", R.string.help);
        startActivity(intent);
    }


    @OnClick(R.id.rl_feedback)
    void onFeedback(View view) {
        Intent intent = FeedbackActivity.getIntent(this);
        startActivity(intent);
    }


    int progress = 50;

    @OnClick(R.id.rl_check_update)
    void onCheckUpdate(View view) {

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("检查中，请稍后...");
        progressDialog.show();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                progressDialog.dismiss();

                new AlertDialog.Builder(SettingActivity.this)
                        .setMessage("检查到新版本，是否更新？")
                        .setPositiveButton("确定", null)
                        .setNegativeButton("取消", null)
                        .show();
            }
        }, 1000);
    }

    @OnClick(R.id.rl_about)
    void onAbout(View view) {
        Intent intent = HtmlActivity.getIntent(this, "", R.string.about);
        startActivity(intent);
    }

    @OnClick(R.id.btn_login)
    void onLogout(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_logout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCollector.removeAllActivity();
                        Intent intent = LoginActivity.getIntent(SettingActivity.this);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
