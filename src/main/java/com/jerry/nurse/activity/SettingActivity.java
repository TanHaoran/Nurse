package com.jerry.nurse.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.AppVersion;
import com.jerry.nurse.model.QQOriginUserInfo;
import com.jerry.nurse.model.QQUserInfo;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.AppUtil;
import com.jerry.nurse.util.DeviceUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.UserUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.tv_cellphone)
    TextView mCellphoneTextView;

    @Bind(R.id.tv_qq)
    TextView mQQTextView;

    @Bind(R.id.tv_wechat)
    TextView mWechatTextView;

    @Bind(R.id.tv_microblog)
    TextView mMicroblogTextView;

    private ProgressDialog mProgressDialog;

    private UserRegisterInfo mUserRegisterInfo;

    // 腾讯官方获取的APPID
    private static final String APP_ID = "1106292702";
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;

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

        // 初始化等待框
        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("请稍后...");

        mUserRegisterInfo = DataSupport.findFirst(UserRegisterInfo.class);

        if (mUserRegisterInfo.getPhone() != null) {
            String cellphone = mUserRegisterInfo.getPhone();
            cellphone = cellphone.substring(0, 2) + "*******" + cellphone.substring(9);
            mCellphoneTextView.setText(cellphone);
        }

        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID, getApplicationContext());
        getQQInfo(mUserRegisterInfo.getRegisterId());
    }

    @OnClick(R.id.rl_qq)
    void onQQ(View view) {
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(SettingActivity.this, "all", mIUiListener);
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

    @OnClick(R.id.rl_check_update)
    void onCheckUpdate(View view) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_APP_VERSION)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
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
                UserUtil.deleteAllInfo(SettingActivity.this);
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
     * 获取用户QQ资料
     */
    private void getQQInfo(final String registerId) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_QQ_NICKNAME)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        if (response.equals("1")) {
                            L.i("QQ昵称为空");
                        } else {
                            updateQQInfo(response);
                        }
                    }
                });
    }

    /**
     * 更新qq显示信息
     *
     * @param nickname
     */
    private void updateQQInfo(String nickname) {

    }

    /********************************QQ第三方登陆********************************************



    /**
     * 自定义监听器实现IUiListener接口后，需要实现的3个方法
     * onComplete完成 onError错误 onCancel取消
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Toast.makeText(SettingActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            L.e("response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                final String openID = obj.getString("openid");
                final String accessToken = obj.getString("access_token");
                final String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                final QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(), qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        QQOriginUserInfo originInfo = new Gson().fromJson(response.toString(), QQOriginUserInfo.class);
                        String deviceId = DeviceUtil.getDeviceId(SettingActivity.this);
                        QQUserInfo info = new QQUserInfo();
                        info.setOpenId(openID);
                        info.setFigureUrl(originInfo.getFigureurl_qq_2());
                        info.setNickName(originInfo.getNickname());
                        info.setProvince(originInfo.getProvince());
                        info.setCity(originInfo.getCity());
                        info.setGender(originInfo.getGender());
                        info.setAccessToken(accessToken);
                        info.setExpires(expires);
                        info.setDeviceId(deviceId);
                        L.e("登录成功" + new Gson().toJson(info));
                        postQQLogin(info);
                    }

                    @Override
                    public void onError(UiError uiError) {
                        L.e("登录失败" + uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        L.e("登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            T.showShort(SettingActivity.this, "授权失败");

        }

        @Override
        public void onCancel() {
            T.showShort(SettingActivity.this, "授权取消");
        }
    }

    /**
     * 使用qq登陆
     *
     * @param info
     */
    private void postQQLogin(QQUserInfo info) {
        mProgressDialog.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.BIND_QQ)
                .content(StringUtil.addModelWithJson(info))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        if (response.equals(REQUEST_SUCCESS)) {
                            T.showShort(SettingActivity.this, "QQ绑定成功");
                            L.i("qq绑定成功");
                        } else {
                            T.showShort(SettingActivity.this, response);
                        }
                    }
                });
    }
}
