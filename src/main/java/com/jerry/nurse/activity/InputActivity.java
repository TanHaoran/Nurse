package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.model.UserInfo;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LitePalUtil;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.ClearEditText;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import okhttp3.MediaType;

import static com.jerry.nurse.activity.ContactDetailActivity.EXTRA_VALIDATE_MESSAGE;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class InputActivity extends BaseActivity {

    public static final String NICKNAME = "昵称";
    public static final String JOB_NUMBER = "工号";
    public static final String VALIDATE_MESSAGE = "验证消息";

    public static final String EXTRA_TITLE = "title";

    @Bind(R.id.tb_input)
    TitleBar mTitleBar;

    @Bind(R.id.cet_input)
    ClearEditText mInputEditText;

    private String mTitle;

    private UserInfo mUserInfo;
    private ProgressDialogManager mProgressDialogManager;

    public static Intent getIntent(Context context, String title) {
        Intent intent = new Intent(context, InputActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    public static Intent getIntent(Context context, int titleRes) {
        String title = context.getResources().getString(titleRes);
        return getIntent(context, title);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_input;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);

        mTitle = getIntent().getStringExtra(EXTRA_TITLE);

        mTitleBar.setTitle(mTitle);

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                String content = mInputEditText.getText().toString();
                if (content.length() > 15) {
                    T.showShort(InputActivity.this, "内容过长");
                    return;
                }
                if (mTitle.equals(NICKNAME)) {
                    String nickname = mInputEditText.getText().toString();
                    postNickname(nickname);
                } else if (mTitle.equals(JOB_NUMBER)) {
                    String jobNumber = mInputEditText.getText().toString();
                    postJobNumber(jobNumber);
                } else if (mTitle.equals(VALIDATE_MESSAGE)) {
                    sendValidateMessage(content);
                }
            }
        });

        mUserInfo = DataSupport.findFirst(UserInfo.class);

        if (mTitle.equals(NICKNAME)) {
            mInputEditText.setText(mUserInfo.getNickName());
        } else if (mTitle.equals(JOB_NUMBER)) {
            mInputEditText.setText(mUserInfo.getEmployeeId());
        }

        // 如果内容为空，就给显示提示输入框
        if (!TextUtils.isEmpty(mInputEditText.getText().toString())) {
            mInputEditText.setHint("请输入" + mTitle);
        }
    }

    /**
     * 发送好友申请
     *
     * @param message
     */
    private void sendValidateMessage(String message) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_VALIDATE_MESSAGE, message);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 设置昵称
     *
     * @param nickname
     */
    void postNickname(final String nickname) {
        mProgressDialogManager.show();
        UserRegisterInfo userRegisterInfo = new UserRegisterInfo();
        userRegisterInfo.setRegisterId(mUserInfo.getRegisterId());
        userRegisterInfo.setNickName(nickname);

        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_REGISTER_INFO)
                .content(StringUtil.addModelWithJson(userRegisterInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("设置昵称成功");
                            // 更新数据库
                            LoginInfo loginInfo = DataSupport.findFirst(LoginInfo.class);
                            loginInfo.setNickName(nickname);
                            LitePalUtil.updateLoginInfo(InputActivity.this, loginInfo);
                            mUserInfo.setNickName(nickname);
                            LitePalUtil.updateUserInfo(InputActivity.this, mUserInfo);
                            finish();
                        } else {
                            L.i("设置昵称失败");
                        }
                    }
                });
    }

    /**
     * 设置工号
     *
     * @param jobNumber
     */
    void postJobNumber(final String jobNumber) {
        mProgressDialogManager.show();
        UserHospitalInfo userHospitalInfo = new UserHospitalInfo();
        userHospitalInfo.setRegisterId(mUserInfo.getRegisterId());
        userHospitalInfo.setEmployeeId(jobNumber);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_HOSPITAL_INFO)
                .content(StringUtil.addModelWithJson(userHospitalInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("设置工号成功");
                            // 保存到数据库
                            mUserInfo.setEmployeeId(jobNumber);
                            LitePalUtil.updateUserInfo(InputActivity.this, mUserInfo);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            L.i("设置工号失败");
                        }
                    }
                });
    }
}
