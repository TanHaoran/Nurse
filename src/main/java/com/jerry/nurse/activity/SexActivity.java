package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.UserBasicInfo;
import com.jerry.nurse.model.UserInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LitePalUtil;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class SexActivity extends BaseActivity {

    public static final String EXTRA_SEX = "sex";
    public static final String SEX_MALE = "男";
    public static final String SEX_FEMALE = "女";

    @Bind(R.id.iv_male)
    ImageView mMaleChooseImageView;

    @Bind(R.id.iv_female)
    ImageView mFemaleChooseImageView;

    private String mSex;

    private UserInfo mUserInfo;
    private ProgressDialogManager mProgressDialogManager;

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
        mProgressDialogManager = new ProgressDialogManager(this);

        mUserInfo = DataSupport.findLast(UserInfo.class);
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
    private void setSex(final String sex) {
        mProgressDialogManager.show();
        UserBasicInfo userBasicInfo = new UserBasicInfo();
        userBasicInfo.setRegisterId(mUserInfo.getRegisterId());
        userBasicInfo.setSex(sex);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_BASIC_INFO)
                .content(StringUtil.addModelWithJson(userBasicInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("设置性别成功");
                            // 更新数据库
                            mUserInfo.setSex(sex);
                            LitePalUtil.updateUserInfo(SexActivity.this, mUserInfo);
                            finish();
                        } else {
                            L.i("设置性别失败");
                        }
                    }
                });
    }
}
