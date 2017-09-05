package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.ThirdPartInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.OnClick;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class HospitalAccountActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, HospitalAccountActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_hospital_account;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    /**
     * 护理不良事件
     *
     * @param view
     */
    @OnClick(R.id.rl_event_report)
    void onEventReport(View view) {
        Intent intent = HospitalLoginActivity.getIntent(this, HospitalLoginActivity.TYPE_EVENT_REPORT_BIND);
        startActivity(intent);
    }

    /**
     * 学分
     *
     * @param view
     */
    @OnClick(R.id.rl_credit)
    void onCredit(View view) {
        Intent intent = HospitalLoginActivity.getIntent(this, HospitalLoginActivity.TYPE_CREDIT_BIND);
        startActivity(intent);
    }

    /**
     * 考试
     *
     * @param view
     */
    @OnClick(R.id.rl_exam)
    void onExam(View view) {
        Intent intent = HospitalLoginActivity.getIntent(this, HospitalLoginActivity.TYPE_EXAM_BIND);
        startActivity(intent);
    }

    /**
     * 解绑账号
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
                        } else {
                            T.showShort(HospitalAccountActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }
}
