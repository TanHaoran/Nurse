package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.User;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.StringUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

public class SexActivity extends BaseActivity {

    @Bind(R.id.iv_male)
    ImageView mMaleChooseImageView;

    @Bind(R.id.iv_female)
    ImageView mFemaleChooseImageView;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SexActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_sex;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }

    @OnClick(R.id.ll_male)
    void onMaleChoose(View view) {
        mProgressDialog.show();
        setSex(0);
        mMaleChooseImageView.setVisibility(View.VISIBLE);
        mFemaleChooseImageView.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.ll_female)
    void onFemaleChoose(View view) {
        mProgressDialog.show();
        setSex(1);
        mMaleChooseImageView.setVisibility(View.INVISIBLE);
        mFemaleChooseImageView.setVisibility(View.VISIBLE);
    }

    /**
     * 设置性别
     * @param sex 0：男, 1:女
     */
    private void setSex(int sex) {
        User user = DataSupport.findFirst(User.class);
        OkHttpUtils.postString()
                .url(ServiceConstant.SET_SEX)
                .content(StringUtil.addModelWithJson(user))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        if (response.equals(REQUEST_SUCCESS)) {

                        }
                    }
                });
    }
}
