package com.jerry.nurse.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.AppVersion;
import com.jerry.nurse.model.BindInfoResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.QQUserInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.AppUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LitePalUtil;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.TencentLoginUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;

import static com.jerry.nurse.activity.SignupActivity.TYPE_BIND_CELLPHONE;
import static com.jerry.nurse.activity.SignupActivity.TYPE_CHANGE_CELLPHONE;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.tv_cellphone)
    TextView mCellphoneTextView;

    @Bind(R.id.tv_qq)
    TextView mQQTextView;

    @Bind(R.id.tv_wechat)
    TextView mWechatTextView;

    @Bind(R.id.tv_microblog)
    TextView mMicroblogTextView;

    private ProgressDialogManager mProgressDialog;

    private LoginInfo mLoginInfo;

    private TencentLoginUtil mTencentLoginUtil;

    private BindInfoResult.BindInfo mBindInfo;


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

        mProgressDialog = new ProgressDialogManager(this);

        mLoginInfo = DataSupport.findFirst(LoginInfo.class);

        // 获取用户所有绑定信息
        getBindInfo(mLoginInfo.getRegisterId());
    }

    @OnClick(R.id.rl_qq)
    void onQQ(View view) {
        if (mBindInfo != null && mBindInfo.getQQOpenId() != null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tips)
                    .setMessage("确定解除绑定 " + mBindInfo.getQQNickName() + " 吗?")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            unBindQQ(mQQUserInfo);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        } else {
            mTencentLoginUtil = new TencentLoginUtil(this) {

                @Override
                public void loginComplete(QQUserInfo info) {
                    bindQQ(info);
                }
            };
            mTencentLoginUtil.login();
        }
    }

    @OnClick(R.id.rl_cellphone)
    void onCellphone(View view) {
        if (!TextUtils.isEmpty(mBindInfo.getPhone())) {
            Intent intent = ChangeCellphoneActivity.getIntent(this, TYPE_CHANGE_CELLPHONE);
            startActivity(intent);
        } else {
            Intent intent = ChangeCellphoneActivity.getIntent(this, TYPE_BIND_CELLPHONE);
            startActivity(intent);
        }
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

    @OnClick(R.id.rl_check_update)
    void onCheckUpdate(View view) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_APP_VERSION)
                .build()
                .execute(new FilterStringCallback(mProgressDialog) {


                    @Override
                    public void onFilterResponse(String response, int id) {
                        try {
                            AppVersion appVersion = new Gson().fromJson(response, AppVersion.class);
                            String localVersion = AppUtil.getVersionName(SettingActivity.this);
                            L.i("本地版本：" + localVersion);
                            L.i("远程版本：" + appVersion.getVersion());
                            if (!localVersion.equals(appVersion.getVersion())) {
                                if (!SettingActivity.this.isFinishing()) {
                                    new AlertDialog.Builder(SettingActivity.this)
                                            .setTitle(R.string.tips)
                                            .setMessage("发现新版本：" + appVersion.getVersion() + ",是否更新?")
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).setNegativeButton(R.string.cancel, null)
                                            .show();
                                }
                            } else {
                                new AlertDialog.Builder(SettingActivity.this)
                                        .setTitle(R.string.tips)
                                        .setMessage(R.string.this_is_the_last_version)
                                        .setPositiveButton(R.string.ok, null)
                                        .show();
                            }
                        } catch (JsonSyntaxException e) {
                            L.i("获取APP版本信息失败");
                            e.printStackTrace();
                        }
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
                        EaseMobLogout();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * 退出环信登陆
     */
    private void EaseMobLogout() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                LitePalUtil.deleteAllInfo(SettingActivity.this);
                try {
                    ActivityCollector.removeAllActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = LoginActivity.getIntent(SettingActivity.this);
                startActivity(intent);
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }

    /**
     * 获取用户所有绑定信息
     */
    private void getBindInfo(final String registerId) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_BIND_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialog) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        BindInfoResult bindInfoResult = new Gson().fromJson(response, BindInfoResult.class);
                        if (bindInfoResult.getCode() == RESPONSE_SUCCESS) {
                            mBindInfo = bindInfoResult.getBody();
                            // 更新界面显示绑定信息
                            updateBindInfo();
                        }
                    }
                });
    }

    /**
     * 更新界面显示绑定信息
     *
     */
    private void updateBindInfo() {
        if (!TextUtils.isEmpty(mBindInfo.getPhone())) {
            String cellphone = mBindInfo.getPhone().substring(0, 2) + "*******" + mBindInfo.getPhone().substring(9);
            mCellphoneTextView.setText(cellphone);
        }
        if (!TextUtils.isEmpty(mBindInfo.getQQOpenId())) {
            mQQTextView.setText(mBindInfo.getQQNickName());
        }
    }

    /**
     * 绑定QQ
     *
     * @param qqUserInfo
     */
    private void bindQQ(final QQUserInfo qqUserInfo) {
//        ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
//        thirdPartInfo.setRegisterId(mUserRegisterInfo.getRegisterId());
//        thirdPartInfo.setOpenId(qqUserInfo.getOpenId());
//        mProgressDialog.show();
//        OkHttpUtils.postString()
//                .url(ServiceConstant.BIND_QQ)
//                .content(StringUtil.addModelWithJson(thirdPartInfo))
//                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .build()
//                .execute(new FilterStringCallback() {
//                    @Override
//                    public void onFilterError(Call call, Exception e, int id) {
//                        mProgressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onFilterResponse(String response, int id) {
//                        mProgressDialog.dismiss();
//                        if (response.equals(REQUEST_SUCCESS)) {
//                            T.showShort(SettingActivity.this, "QQ绑定成功");
//                            L.i("qq绑定成功");
//                            updateQQInfo(qqUserInfo);
//                        } else {
//                            T.showShort(SettingActivity.this, response);
//                        }
//                    }
//                });
    }


    /**
     * 解绑QQ
     *
     * @param qqUserInfo
     */
    private void unBindQQ(QQUserInfo qqUserInfo) {
//        ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
//        thirdPartInfo.setRegisterId(mUserRegisterInfo.getRegisterId());
//        thirdPartInfo.setOpenId(qqUserInfo.getOpenId());
//        mProgressDialog.show();
//        OkHttpUtils.postString()
//                .url(ServiceConstant.UN_BIND_QQ)
//                .content(StringUtil.addModelWithJson(thirdPartInfo))
//                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .build()
//                .execute(new FilterStringCallback() {
//                    @Override
//                    public void onFilterError(Call call, Exception e, int id) {
//                        mProgressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onFilterResponse(String response, int id) {
//                        mProgressDialog.dismiss();
//                        if (response.equals(REQUEST_SUCCESS)) {
//                            T.showShort(SettingActivity.this, "QQ解绑成功");
//                            L.i("qq解绑成功");
//                            updateBindInfo();
//                        } else {
//                            T.showShort(SettingActivity.this, response);
//                        }
//                    }
//                });
    }

    /**
     * 解绑手机
     *
     * @param cellphone
     */
    private void unBindCellphone(String cellphone) {
//        ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
//        thirdPartInfo.setRegisterId(mUserRegisterInfo.getRegisterId());
//        thirdPartInfo.setPhone(cellphone);
//        mProgressDialog.show();
//        OkHttpUtils.postString()
//                .url(ServiceConstant.UN_BIND_QQ)
//                .content(StringUtil.addModelWithJson(thirdPartInfo))
//                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .build()
//                .execute(new FilterStringCallback() {
//                    @Override
//                    public void onFilterError(Call call, Exception e, int id) {
//                        mProgressDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onFilterResponse(String response, int id) {
//                        mProgressDialog.dismiss();
//                        if (response.equals(REQUEST_SUCCESS)) {
//                            T.showShort(SettingActivity.this, "手机解绑成功");
//                            L.i("手机解绑成功");
//                            updateCellphoneInfo(null);
//                        } else {
//                            T.showShort(SettingActivity.this, response);
//                        }
//                    }
//                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // 腾讯的第三方登陆
//        if (requestCode == Constants.REQUEST_LOGIN) {
//            Tencent.onActivityResultData(requestCode, resultCode, data, mTencentLoginUtil.getIUiListener());
//        }
//        updateCellphoneInfo(mUserRegisterInfo.getPhone());
//        super.onActivityResult(requestCode, resultCode, data);
    }
}
