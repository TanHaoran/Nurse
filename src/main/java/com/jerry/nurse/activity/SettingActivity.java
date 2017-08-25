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

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.model.BindInfoResult;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.Qq;
import com.jerry.nurse.model.ThirdPartInfo;
import com.jerry.nurse.model.VersionResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.AppUtil;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.DownloadUtil;
import com.jerry.nurse.util.EaseMobManager;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LitePalUtil;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.TencentLoginUtil;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
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
        // 解绑qq
        L.i("绑定的数量是：" + mBindInfo.getBindCount());
        // 绑定qq
        if (TextUtils.isEmpty(mBindInfo.getQQOpenId())) {
            mTencentLoginUtil = new TencentLoginUtil(this) {
                @Override
                public void loginComplete(Qq info) {
                    bindQQ(info);
                }
            };
            mTencentLoginUtil.login();

        } else if (!TextUtils.isEmpty(mBindInfo.getQQOpenId()) && mBindInfo.getBindCount() > 1) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tips)
                    .setMessage("确定解除绑定 " + mBindInfo.getQQNickName() + " 吗?")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            unBindQQ();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    @OnClick(R.id.rl_change_password)
    void onChangePassword(View view) {
        Intent intent = ChangePasswordActivity.getIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.rl_help)
    void onHelp(View view) {
        Intent intent = HtmlActivity.getIntent(this, "https://www.baidu.com", "帮助");
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
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_APP_VERSION)
                .build()
                .execute(new FilterStringCallback(mProgressDialog) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        final VersionResult versionResult = new Gson().fromJson(response, VersionResult.class);
                        if (versionResult.getCode() == RESPONSE_SUCCESS) {

                            String localVersion = AppUtil.getVersionName(SettingActivity.this);
                            if (!localVersion.equals(versionResult.getBody().getVersion())) {
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
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("智护下载");
        builder.setContentText("正在下载");
        final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0x101, builder.build());
        builder.setProgress(100, 0, false);

        BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
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
                                DownloadUtil.get().openFile(file);
                            }

                            @Override
                            public void onDownloading(int progress) {
                                L.i("下载进度:" + progress);
                                builder.setProgress(100, progress, false);
                                manager.notify(PROGRESS_NOTIFICATION_ID, builder.build());
                                //下载进度提示
                                builder.setContentText("下载" + progress + "%");
                            }

                            @Override
                            public void onDownloadFailed() {
                                L.i("下载失败");
                            }
                        });
                T.showShort(SettingActivity.this, "已转入后台下载");
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
        mProgressDialog.show();

        // 清除掉包名下DOCUMENTS中所有的文件
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File[] files = dir.listFiles();
        for (File file : files) {
            file.delete();
        }

        mProgressDialog.dismiss();
        showShort(SettingActivity.this, R.string.clear_finish);
    }

    @OnClick(R.id.rl_about)
    void onAbout(View view) {
        Intent intent = HtmlActivity.getIntent(this, "", "关于");
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
        EaseMobManager easeMobManager = new EaseMobManager(this) {
            @Override
            protected void onLogoutSuccess() {
                LitePalUtil.deleteAllInfo(SettingActivity.this);
                try {
                    ActivityCollector.removeAllActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = LoginActivity.getIntent(SettingActivity.this);
                startActivity(intent);
            }
        };
        easeMobManager.logout();
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
                            updateBindInfo(mBindInfo);
                        } else {
                            showShort(SettingActivity.this, bindInfoResult.getMsg());
                        }
                    }
                });
    }

    /**
     * 更新界面显示绑定信息
     */
    private void updateBindInfo(BindInfoResult.BindInfo bindInfo) {
        if (!TextUtils.isEmpty(bindInfo.getPhone())) {
            if (bindInfo.getPhone().length() == 11) {
                String cellphone = bindInfo.getPhone().substring(0, 2) + "*******" + mBindInfo.getPhone().substring(9);
                mCellphoneTextView.setText(cellphone);
            }
        } else {
            mCellphoneTextView.setText("");
        }
        if (!TextUtils.isEmpty(bindInfo.getQQOpenId())) {
            mQQTextView.setText(bindInfo.getQQNickName());
        } else {
            mQQTextView.setText("");
        }
    }

    /**
     * 绑定QQ
     *
     * @param qq
     */
    private void bindQQ(final Qq qq) {
        ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
        thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
        thirdPartInfo.setQQData(qq);
        mProgressDialog.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.BIND)
                .content(StringUtil.addModelWithJson(thirdPartInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialog) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            T.showShort(SettingActivity.this, "QQ绑定成功");
                            L.i("qq绑定成功");
                            // 获取用户所有绑定信息
                            mBindInfo.setQQOpenId(qq.getOpenId());
                            mBindInfo.setQQNickName(qq.getNickName());
                            mBindInfo.setBindCount(mBindInfo.getBindCount() + 1);
                            updateBindInfo(mBindInfo);
                        } else {
                            T.showShort(SettingActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }


    /**
     * 解绑QQ
     */
    private void unBindQQ() {
        ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
        thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
        Qq qq = new Qq();
        qq.setOpenId(mBindInfo.getQQOpenId());
        thirdPartInfo.setQQData(qq);
        mProgressDialog.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.UNBIND)
                .content(StringUtil.addModelWithJson(thirdPartInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialog) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            showShort(SettingActivity.this, "QQ解绑成功");
                            L.i("qq解绑成功");
                            mBindInfo.setQQNickName("");
                            mBindInfo.setQQOpenId("");
                            mBindInfo.setBindCount(mBindInfo.getBindCount() - 1);
                            updateBindInfo(mBindInfo);
                        } else {
                            showShort(SettingActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 腾讯的第三方登陆
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mTencentLoginUtil.getIUiListener());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
