package com.jerry.nurse.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.app.MyApplication;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.model.BindInfo;
import com.jerry.nurse.model.BindInfoResult;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.MicroBlog;
import com.jerry.nurse.model.MicroBlogResult;
import com.jerry.nurse.model.Qq;
import com.jerry.nurse.model.ThirdPartInfo;
import com.jerry.nurse.model.VersionResult;
import com.jerry.nurse.model.WeChat;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.AppUtil;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.DownloadUtil;
import com.jerry.nurse.util.EaseMobManager;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LitePalUtil;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.TencentLoginManager;
import com.jerry.nurse.view.ToggleButton;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.Tencent;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.APK_ADDRESS;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;
import static com.jerry.nurse.util.T.showShort;

public class SettingActivity extends BaseActivity {

    private static final int PROGRESS_NOTIFICATION_ID = 0x101;

    @Bind(R.id.tv_cellphone)
    TextView mCellphoneTextView;

    @Bind(R.id.tv_qq)
    TextView mQQTextView;

    @Bind(R.id.tv_wechat)
    TextView mWechatTextView;

    @Bind(R.id.tv_microblog)
    TextView mMicroblogTextView;

    @Bind(R.id.tv_hospital_account)
    TextView mHospitalAccountTextView;

    @Bind(R.id.tb_alarm)
    ToggleButton mAlarmButton;


    private LoginInfo mLoginInfo;

    private TencentLoginManager mTencentLoginManager;

    private BindInfo mBindInfo;

    private SsoHandler mSsoHandler;

    private boolean mAlarmOn;

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
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        // 初始化微博登录的对象
        mSsoHandler = new SsoHandler(this);
        mAlarmOn = (boolean) SPUtil.get(this, SPUtil.ALARM_ON, true);
        L.i("读取到的值是:" + mAlarmOn);
        mAlarmButton.setOpen(mAlarmOn);
        mAlarmButton.setOnToggleListener(new ToggleButton.OnToggleListener() {
            @Override
            public void onToggle(boolean open) {
                SPUtil.put(SettingActivity.this, SPUtil.ALARM_ON, open);
                L.i("设置的开关值是:" + open);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取用户所有绑定信息
        getBindInfo(mLoginInfo.getRegisterId());
    }

    /**
     * 修改/解绑/绑定 手机号
     *
     * @param view
     */
    @OnClick(R.id.rl_cellphone)
    void onCellphone(View view) {
        Intent intent = ChangeCellphoneActivity.getIntent(this, mBindInfo);
        startActivity(intent);
    }

    /**
     * 绑定/解绑 QQ
     *
     * @param view
     */
    @OnClick(R.id.rl_qq)
    void onQQ(View view) {
        // 绑定qq
        if (TextUtils.isEmpty(mBindInfo.getQQOpenId())) {
            mTencentLoginManager = new TencentLoginManager(this) {
                @Override
                public void loginComplete(Qq info) {
                    ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
                    thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
                    thirdPartInfo.setQQData(info);
                    thirdPartInfo.setLoginType(ThirdPartInfo.TYPE_QQ);
                    bind(thirdPartInfo);
                }
            };
            mTencentLoginManager.login();

        }
        // 解绑qq
        else if (!TextUtils.isEmpty(mBindInfo.getQQOpenId()) && mBindInfo.getBindCount() > 1) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tips)
                    .setMessage("确定解除绑定 " + mBindInfo.getQQNickName() + " 吗?")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
                            thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
                            Qq qq = new Qq();
                            qq.setOpenId(mBindInfo.getQQOpenId());
                            thirdPartInfo.setQQData(qq);
                            thirdPartInfo.setLoginType(ThirdPartInfo.TYPE_QQ);
                            unBind(thirdPartInfo);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    /**
     * 绑定/解绑 微信
     *
     * @param view
     */
    @OnClick(R.id.rl_weixin)
    void onWeChat(View view) {
        // 绑定微信
        if (TextUtils.isEmpty(mBindInfo.getWeixinOpenId())) {
            if (!MyApplication.sWxApi.isWXAppInstalled()) {
                // 未安装微信
                showShort(this, R.string.wechat_not_install);
                return;
            }
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "diandi_wx_login";
            MyApplication.sWxApi.sendReq(req);

        }
        // 解绑微信
        else if (!TextUtils.isEmpty(mBindInfo.getWeixinOpenId()) && mBindInfo.getBindCount() > 1) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tips)
                    .setMessage("确定解除绑定 " + mBindInfo.getWeixinNickName() + " 吗?")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
                            thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
                            WeChat weChat = new WeChat();
                            weChat.setOpenId(mBindInfo.getWeixinOpenId());
                            thirdPartInfo.setWXData(weChat);
                            thirdPartInfo.setLoginType(ThirdPartInfo.TYPE_WE_CHAT);
                            unBind(thirdPartInfo);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    /**
     * 绑定/解绑微博
     *
     * @param view
     */
    @OnClick(R.id.rl_microblog)
    void onMicroBlog(View view) {
        // 绑定微博
        if (TextUtils.isEmpty(mBindInfo.getWeiboOpenId())) {
            mSsoHandler.authorize(new SelfWbAuthListener());
        }
        // 解绑微博
        else if (!TextUtils.isEmpty(mBindInfo.getWeiboOpenId()) && mBindInfo.getBindCount() > 1) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tips)
                    .setMessage("确定解除绑定 " + mBindInfo.getWeiboNickName() + " 吗?")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
                            thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
                            MicroBlog microBlog = new MicroBlog();
                            microBlog.setIdstr(mBindInfo.getWeiboOpenId());
                            thirdPartInfo.setWBData(microBlog);
                            thirdPartInfo.setLoginType(ThirdPartInfo.TYPE_MICRO_BLOG);
                            unBind(thirdPartInfo);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Oauth2AccessToken mAccessToken = token;
                    L.i("Token:" + mAccessToken.getToken());
                    L.i("Uid:" + mAccessToken.getUid());
                    if (mAccessToken.isSessionValid()) {
                        L.i("授权成功");
                        //获取个人资料
                        OkHttpUtils.get()
                                .url(ServiceConstant.MICRO_BLOG_GET_USER_INFO)
                                .addParams("access_token", mAccessToken.getToken())
                                .addParams("uid", mAccessToken.getUid())
                                .build()
                                .execute(new FilterStringCallback() {

                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        L.i("获取失败：" + e.getMessage());
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onFilterResponse(String response, int id) {
                                        int start = response.indexOf("source");
                                        int end = response.indexOf(",\"favorited");
                                        String href = response.substring(start, end);
                                        response = response.replace(href, "source\":\"\"");
                                        L.i("处理后结果:" + response);
                                        MicroBlogResult result = new Gson().fromJson(response, MicroBlogResult.class);
                                        // 微博绑定

                                        MicroBlog microBlog = new MicroBlog();
                                        microBlog.setIdstr(result.getIdstr());
                                        microBlog.setName(result.getName());
                                        microBlog.setLocation(result.getLocation());
                                        microBlog.setDescription(result.getDescription());
                                        microBlog.setProfile_image_url(result.getProfile_image_url());
                                        microBlog.setGender(result.getGender());
                                        // 设置极光推送Id
                                        microBlog.setDeviceRegId(JPushInterface.getRegistrationID(SettingActivity.this));

                                        ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
                                        thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
                                        thirdPartInfo.setWBData(microBlog);
                                        thirdPartInfo.setLoginType(ThirdPartInfo.TYPE_MICRO_BLOG);
                                        bind(thirdPartInfo);
                                    }
                                });

                    }
                }
            });
        }

        @Override
        public void cancel() {
            L.i("取消授权");
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(SettingActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 院内账号绑定/解绑
     *
     * @param view
     */
    @OnClick(R.id.rl_hospital_account)
    void onHospitalAccount(View view) {
        Intent intent = HospitalAccountActivity.getIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.rl_change_password)
    void onChangePassword(View view) {
        Intent intent = ChangePasswordActivity.getIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.rl_help)
    void onHelp(View view) {
        Intent intent = HtmlActivity.getIntent(this, ServiceConstant.HELP_URL, "帮助");
        startActivity(intent);
    }


    @OnClick(R.id.rl_feedback)
    void onFeedback(View view) {
        Intent intent = FeedbackActivity.getIntent(this);
        startActivity(intent);
    }

    /**
     * 检查更新
     *
     * @param view
     */
    @OnClick(R.id.rl_check_update)
    void onCheckUpdate(View view) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_APP_VERSION)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        final VersionResult versionResult = new Gson().fromJson(response, VersionResult.class);
                        if (versionResult.getCode() == RESPONSE_SUCCESS) {

                            String localVersion = AppUtil.getVersionName(SettingActivity.this);
                            if (!localVersion.equals(versionResult.getBody().getVersion() + "")) {
                                if (!SettingActivity.this.isFinishing()) {
                                    new AlertDialog.Builder(SettingActivity.this)
                                            .setTitle(R.string.tips)
                                            .setMessage("发现新版本:" + versionResult.getBody().getVersion() +
                                                    "\n更新时间: " +
                                                    DateUtil.parseMysqlDateToString(versionResult.getBody().getReleaseTime()) +
                                                    "\n更新内容:" + versionResult.getBody().getUpdateContent() + "\n是否更新?")
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    downloadApk(versionResult.getBody().getReleaseUrl());
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
                        } else {
                            showShort(SettingActivity.this, versionResult.getMsg());
                        }
                    }
                });
    }

    /**
     * 下载最新版本的Apk
     *
     * @param webName
     */
    private void downloadApk(final String webName) {
        // 创建顶部通知框
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.topnew_icon);
        builder.setContentTitle("燕尾帽下载");
        builder.setContentText("正在下载");
        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0x101, builder.build());
        builder.setProgress(100, 0, false);

        BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
                final int[] nowProgress = {-1};
                DownloadUtil.get().download(APK_ADDRESS + webName, new Date().getTime() + ".apk",
                        new DownloadUtil.OnDownloadListener() {
                            @Override
                            public void onDownloadSuccess(File file) {
                                L.i("下载完成");

                                //下载完成后更改标题以及提示信息
                                builder.setContentText("下载完成");
                                //设置进度为完成
                                builder.setProgress(100, 100, false);
                                manager.notify(PROGRESS_NOTIFICATION_ID, builder.build());

                                if (!file.exists()) {
                                    L.i("文件不存在");
                                }

                                DownloadUtil.get().openFile(SettingActivity.this, file);
                            }

                            @Override
                            public void onDownloading(int progress) {
                                if (progress > nowProgress[0]) {
                                    nowProgress[0] = progress;
                                    L.i("下载进度:" + progress);
                                    builder.setProgress(100, progress, false);
                                    manager.notify(PROGRESS_NOTIFICATION_ID, builder.build());
                                    //下载进度提示
                                    builder.setContentText("下载" + progress + "%");
                                }
                            }

                            @Override
                            public void onDownloadFailed(Call call, Exception e) {
                                L.i("下载失败");
                            }
                        });
                showShort(SettingActivity.this, "已转入后台下载");
            }

            @Override
            public void onDenied(List<String> deniedPermission) {

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
        mProgressDialogManager.show();

        // 清除掉包名下DOCUMENTS中所有的文件
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
        }

        mProgressDialogManager.dismiss();
        T.showShort(SettingActivity.this, R.string.clear_finish);
    }

    @OnClick(R.id.rl_about)
    void onAbout(View view) {
        Intent intent = HtmlActivity.getIntent(this, ServiceConstant.ABOUT_URL, "关于");
        startActivity(intent);
    }

    @OnClick(R.id.btn_login)
    void onLogout(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_logout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 退出环信
                        new EaseMobManager().logout();

                        // 清除数据库
                        LitePalUtil.deleteAllInfo(SettingActivity.this);
                        try {
                            ActivityCollector.removeAllActivity();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = LoginActivity.getIntent(SettingActivity.this);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * 获取用户所有绑定信息
     */
    private void getBindInfo(final String registerId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_BIND_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        BindInfoResult bindInfoResult = new Gson().fromJson(response, BindInfoResult.class);
                        if (bindInfoResult.getCode() == RESPONSE_SUCCESS) {
                            mBindInfo = bindInfoResult.getBody();
                            // 更新界面显示绑定信息
                            updateBindInfo(mBindInfo);
                        } else {
                            mBindInfo = new BindInfo();
                            showShort(SettingActivity.this, bindInfoResult.getMsg());
                        }
                    }
                });
    }


    /**
     * 更新界面显示绑定信息
     */
    private void updateBindInfo(BindInfo bindInfo) {
        if (!TextUtils.isEmpty(bindInfo.getPhone())) {
            if (bindInfo.getPhone().length() == 11) {
                String cellphone = bindInfo.getPhone().substring(0, 2) + "*******" + mBindInfo.getPhone().substring(9);
                mCellphoneTextView.setText(cellphone);
            }
        } else {
            mCellphoneTextView.setText("");
        }
        // 根据QQ的OpenId来判断是否有QQ资料
        if (!TextUtils.isEmpty(bindInfo.getQQOpenId())) {
            mQQTextView.setText(bindInfo.getQQNickName());
        } else {
            mQQTextView.setText("");
        }
        // 根据微信的OpenId来判断是否有微信资料
        if (!TextUtils.isEmpty(bindInfo.getWeixinOpenId())) {
            mWechatTextView.setText(bindInfo.getWeixinNickName());
        } else {
            mWechatTextView.setText("");
        }
        // 根据微博的用户名Id来判断是否有微信资料
        if (!TextUtils.isEmpty(bindInfo.getWeiboOpenId())) {
            mMicroblogTextView.setText(bindInfo.getWeiboNickName());
        } else {
            mMicroblogTextView.setText("");
        }

        // 显示是否绑定院内账号
        if (!TextUtils.isEmpty(bindInfo.getBLSJOpenId()) || !TextUtils.isEmpty(bindInfo.getPBOpenId()) ||
                !TextUtils.isEmpty(bindInfo.getXFOpenId())) {
            mHospitalAccountTextView.setText("已绑定");
        } else {
            mHospitalAccountTextView.setText("");
        }
    }

    /**
     * 绑定账号
     *
     * @param thirdPartInfo
     */
    private void bind(final ThirdPartInfo thirdPartInfo) {
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.BIND)
                .content(StringUtil.addModelWithJson(thirdPartInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            showShort(SettingActivity.this, "账号绑定成功");
                            L.i("账号绑定成功");
                            // 获取用户所有绑定信息
                            getBindInfo(mBindInfo.getRegisterId());
                        } else {
                            showShort(SettingActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }

    /**
     * 解绑微信
     */
    private void unBind(ThirdPartInfo thirdPartInfo) {
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.UNBIND)
                .content(StringUtil.addModelWithJson(thirdPartInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            T.showShort(SettingActivity.this, "解绑成功");
                            L.i("解绑成功");
                            getBindInfo(mLoginInfo.getRegisterId());
                        } else {
                            T.showShort(SettingActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 处理微博
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

        // 腾讯的第三方登陆
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mTencentLoginManager.getIUiListener());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
