package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.Feedback;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class Feedback3Activity extends BaseActivity {

    @Bind(R.id.tb_feedback)
    TitleBar mTitleBar;

    @Bind(R.id.et_feedback)
    EditText mFeedback;

    private String lastChoice;

    public static final String EXTRA_CONTENT = "feedback_content";

    public static Intent getIntent(Context context, String content) {
        Intent intent = new Intent(context, Feedback3Activity.class);
        intent.putExtra(EXTRA_CONTENT, content);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_feedback3;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        lastChoice = getIntent().getStringExtra(EXTRA_CONTENT);

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                if (TextUtils.isEmpty(mFeedback.getText().toString())) {
                    T.showShort(Feedback3Activity.this, "请填写你宝贵的反馈内容哦！");
                    return;
                }
                sendFeedback(lastChoice + mFeedback.getText().toString());
            }
        });

    }

    /**
     * 提交反馈信息
     *
     * @param content
     */
    void sendFeedback(final String content) {
        mProgressDialogManager.show();
        String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        Feedback feedback = new Feedback(registerId, content);
        OkHttpUtils.postString()
                .url(ServiceConstant.SEND_FEEDBACK)
                .content(StringUtil.addModelWithJson(feedback))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            T.showShort(Feedback3Activity.this, R.string.submit_success);
                            ActivityCollector.removeLastActivity(3);
                            finish();
                        } else {
                            T.showShort(Feedback3Activity.this, commonResult.getMsg());
                        }
                    }
                });
    }
}
