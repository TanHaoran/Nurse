package com.jerry.nurse.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.OnClick;
import okhttp3.Call;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

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

    @OnClick(R.id.rl_cellphone)
    void onCellphone(View view) {
        Intent intent = ChangeCellphoneActivity.getIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.rl_change_password)
    void onChangePassword(View view) {
        Intent intent = ChangePasswordActivity.getIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.rl_help)
    void onHelp(View view) {
        Intent intent = HtmlActivity.getIntent(this, "");
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

        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_APP_VERSION)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        if (response.equals(REQUEST_SUCCESS)) {
                        }
                        new AlertDialog.Builder(SettingActivity.this)
                                .setMessage("检查到新版本，是否更新？")
                                .setPositiveButton("确定", null)
                                .setNegativeButton("取消", null)
                                .show();
                    }
                });
    }

    /**
     * 清除缓存
     *
     * @param view
     */
    @OnClick(R.id.rl_clear_cache)
    void onClearCache(View view) {
        mProgressDialog.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mProgressDialog.dismiss();
                T.showShort(SettingActivity.this, R.string.clear_finish);
            }
        }, 1000);
    }

    @OnClick(R.id.rl_about)
    void onAbout(View view) {
        Intent intent = HtmlActivity.getIntent(this, "");
        startActivity(intent);
    }

    @OnClick(R.id.btn_login)
    void onLogout(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_logout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserUtil.deleteAllInfo(SettingActivity.this);
                        ActivityCollector.removeAllActivity();
                        Intent intent = LoginActivity.getIntent(SettingActivity.this);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
