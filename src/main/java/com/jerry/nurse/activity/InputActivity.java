package com.jerry.nurse.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.ClearEditText;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

public class InputActivity extends BaseActivity {

    public static final String NICKNAME = "昵称";
    public static final String JOB_NUMBER = "工号";

    public static final String EXTRA_TITLE = "title";

    @Bind(R.id.tb_input)
    TitleBar mTitleBar;

    @Bind(R.id.cet_input)
    ClearEditText mInputEditText;

    private String mTitle;

    private UserRegisterInfo mUserRegisterInfo;
    private UserHospitalInfo mUserHospitalInfo;
    private ProgressDialog mProgressDialog;

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

        // 初始化等待框
        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("请稍后...");

        mTitle = getIntent().getStringExtra(EXTRA_TITLE);

        mTitleBar.setTitle(mTitle);
        mInputEditText.setHint("请输入" + mTitle);

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
                }
            }
        });

        if (mTitle.equals(NICKNAME)) {
            mUserRegisterInfo = DataSupport.findLast(UserRegisterInfo.class);
            mInputEditText.setText(mUserRegisterInfo.getNickName());
        } else if (mTitle.equals(JOB_NUMBER)) {
            mUserHospitalInfo = DataSupport.findLast(UserHospitalInfo.class);
            mInputEditText.setText(mUserHospitalInfo.getEmployeeId());
        }

    }

    /**
     * 设置昵称
     *
     * @param nickname
     */
    void postNickname(final String nickname) {
        mProgressDialog.show();
        mUserRegisterInfo.setNickName(nickname);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_REGISTER_INFO)
                .content(StringUtil.addModelWithJson(mUserRegisterInfo))
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
                            L.i("设置昵称成功");
                            // 设置成功后更新数据库
//                            LitePalUtil.saveRegisterInfo(InputActivity.this, mUserRegisterInfo);
                            setResult(RESULT_OK);
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
        mProgressDialog.show();
        mUserHospitalInfo.setEmployeeId(jobNumber);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_HOSPITAL_INFO)
                .content(StringUtil.addModelWithJson(mUserHospitalInfo))
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
                            L.i("设置工号成功");
                            // 设置成功后更新数据库
//                            LitePalUtil.saveHospitalInfo(mUserHospitalInfo);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            T.showShort(InputActivity.this, "设置工号失败");
                            L.i("设置工号失败");
                        }
                    }
                });
    }
}
