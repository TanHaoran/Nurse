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
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class FeedbackActivity extends BaseActivity {

    @Bind(R.id.tb_feedback)
    TitleBar mTitleBar;

    @Bind(R.id.et_feedback)
    EditText mFeedbackEditText;
    private ProgressDialogManager mProgressDialogManager;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                String content = mFeedbackEditText.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    T.showShort(FeedbackActivity.this, R.string.feedback_empty);
                    return;
                }
                sendFeedback(content);
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
                            T.showShort(FeedbackActivity.this, R.string.submit_success);
                            finish();
                        } else {
                            T.showShort(FeedbackActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }
}
