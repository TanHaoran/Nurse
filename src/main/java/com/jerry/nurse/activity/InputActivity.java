package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.constant.ExtraValue;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.User;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.UserUtil;
import com.jerry.nurse.view.ClearEditText;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.activity.PersonalInfoActivity.REQUEST_NICKNAME;
import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

public class InputActivity extends BaseActivity {

    public static final String NICKNAME = "昵称";

    @Bind(R.id.tb_input)
    TitleBar mTitleBar;

    @Bind(R.id.cet_input)
    ClearEditText mInputEditText;

    private String mTitle;

    public static Intent getIntent(Context context, String title) {
        Intent intent = new Intent(context, InputActivity.class);
        intent.putExtra(ExtraValue.EXTRA_TITLE, title);
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

        mTitle = getIntent().getStringExtra(ExtraValue.EXTRA_TITLE);

        mTitleBar.setTitle(mTitle);
        mInputEditText.setHint("请输入" + mTitle);

        mTitleBar.setRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_save)
    void onSave(View view) {
        if (mTitle.equals(NICKNAME)) {
            String nickname = mInputEditText.getText().toString();
            updateNickname(nickname);
        } else {

        }
    }

    /**
     * 设置昵称
     *
     * @param nickname
     */
    void updateNickname(final String nickname) {
        mProgressDialog.show();
        User user = DataSupport.findFirst(User.class);
        user.setNickName(nickname);
        OkHttpUtils.postString()
                .url(ServiceConstant.SET_NICKNAME)
                .content(StringUtil.addModelWithJson(user))
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
                            User user = DataSupport.findFirst(User.class);
                            user.setNickName(nickname);
                            UserUtil.updateUser(InputActivity.this, user);
                            setResult(REQUEST_NICKNAME);
                            finish();
                        } else {
                            L.i("设置昵称失败");
                        }
                    }
                });
    }
}
