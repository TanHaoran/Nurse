package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.HospitalResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class ChangeActivity extends BaseActivity {

    @Bind(R.id.iv_report)
    ImageView mReportCheck;

    @Bind(R.id.iv_audit)
    ImageView mAuditCheck;

    private int mRole = 0;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ChangeActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_change;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        mRole = (int) SPUtil.get(this, SPUtil.ROLE, 0);

        if (mRole == 0) {
            mReportCheck.setVisibility(View.VISIBLE);
        } else {
            mAuditCheck.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.ll_report)
    void onChangeReport(View view) {
        LoginInfo loginInfo = DataSupport.findFirst(LoginInfo.class);
        mReportCheck.setVisibility(View.VISIBLE);
        mAuditCheck.setVisibility(View.INVISIBLE);
        meetingChange(loginInfo.getReguserId(), 0);
    }

    @OnClick(R.id.ll_audit)
    void onChangeAudit(View view) {
        LoginInfo loginInfo = DataSupport.findFirst(LoginInfo.class);
        mAuditCheck.setVisibility(View.VISIBLE);
        mReportCheck.setVisibility(View.INVISIBLE);
        meetingChange(loginInfo.getReguserId(), 1);
    }

    /**
     * 切换身份
     *
     * @param reguserId
     * @param role
     */
    private void meetingChange(final String reguserId, final int role) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.MEETING_CHANGE)
                .addParams("reguserId", reguserId)
                .addParams("role", String.valueOf(role))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        HospitalResult result = new Gson().fromJson(response, HospitalResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            SPUtil.put(ChangeActivity.this, SPUtil.ROLE, role);
                        }
                        finish();
                    }
                });
    }
}
