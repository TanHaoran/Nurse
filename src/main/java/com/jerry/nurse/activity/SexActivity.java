package com.jerry.nurse.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.UserBasicInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

public class SexActivity extends BaseActivity {

    public static final String EXTRA_SEX = "sex";
    public static final String SEX_MALE = "男";
    public static final String SEX_FEMALE = "女";

    @Bind(R.id.iv_male)
    ImageView mMaleChooseImageView;

    @Bind(R.id.iv_female)
    ImageView mFemaleChooseImageView;

    private String mSex;

    private UserBasicInfo userBasicInfo;
    private ProgressDialog mProgressDialog;

    public static Intent getIntent(Context context, String sex) {
        Intent intent = new Intent(context, SexActivity.class);
        intent.putExtra(EXTRA_SEX, sex);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_sex;
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

        userBasicInfo = DataSupport.findLast(UserBasicInfo.class);
        mSex = getIntent().getStringExtra(EXTRA_SEX);
        if (SEX_MALE.equals(mSex)) {
            mMaleChooseImageView.setVisibility(View.VISIBLE);
        } else if (SEX_FEMALE.equals(mSex)) {
            mFemaleChooseImageView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.ll_male)
    void onMaleChoose(View view) {
        mMaleChooseImageView.setVisibility(View.VISIBLE);
        mFemaleChooseImageView.setVisibility(View.INVISIBLE);
        setSex(SEX_MALE);
    }

    @OnClick(R.id.ll_female)
    void onFemaleChoose(View view) {
        mMaleChooseImageView.setVisibility(View.INVISIBLE);
        mFemaleChooseImageView.setVisibility(View.VISIBLE);
        setSex(SEX_FEMALE);
    }

    /**
     * 设置性别
     *
     * @param sex
     */
    private void setSex(String sex) {
        mProgressDialog.show();
        userBasicInfo.setSex(sex);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_BASIC_INFO)
                .content(StringUtil.addModelWithJson(userBasicInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        if (response.equals(REQUEST_SUCCESS)) {
                            // 首先更新UI界面
                            UserUtil.saveBasicInfo(userBasicInfo);
                            setResult(RESULT_OK);
                        } else {
                            L.i("修改性别信息失败");
                        }
                        finish();
                    }
                });
    }
}
