package com.jerry.nurse.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.BindInfo;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.ThirdPartInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class HospitalAccountActivity extends BaseActivity {

    public static final String EXTRA_BIND_INFO = "extra_bind_info";

    @Bind(R.id.tv_event_report)
    TextView mEventReportTextView;

    @Bind(R.id.tv_credit)
    TextView mCreditTextView;

    @Bind(R.id.tv_schedule)
    TextView mScheduleTextView;

    private BindInfo mBindInfo;

    public static Intent getIntent(Context context, BindInfo bindInfo) {
        Intent intent = new Intent(context, HospitalAccountActivity.class);
        intent.putExtra(EXTRA_BIND_INFO, bindInfo);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_hospital_account;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mBindInfo = (BindInfo) getIntent().getSerializableExtra(EXTRA_BIND_INFO);
        setBindData(mBindInfo);
    }

    private void setBindData(BindInfo bindInfo) {
        if (!TextUtils.isEmpty(bindInfo.getBLSJOpenId())) {
            mEventReportTextView.setText(bindInfo.getBLSJId());
        }
        mCreditTextView.setText("");
        mScheduleTextView.setText("");
    }

    /**
     * 护理不良事件
     *
     * @param view
     */
    @OnClick(R.id.rl_event_report)
    void onEventReport(View view) {
        // 绑定护理不良事件
        if (TextUtils.isEmpty(mBindInfo.getBLSJOpenId())) {
            Intent intent = HospitalLoginActivity.getIntent(this, HospitalLoginActivity.TYPE_BIND, ThirdPartInfo.TYPE_EVENT_REPORT);
            startActivity(intent);
        }
        // 解绑护理不良事件
        else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tips)
                    .setMessage("确定解除绑定 " + mBindInfo.getBLSJId() + " 吗?")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
                            thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
                            thirdPartInfo.setLoginName(mBindInfo.getBLSJId());
                            thirdPartInfo.setLoginType(ThirdPartInfo.TYPE_QQ);
                            unBind(thirdPartInfo);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    /**
     * 学分
     *
     * @param view
     */
    @OnClick(R.id.rl_credit)
    void onCredit(View view) {
        // 绑定学分
        if (TextUtils.isEmpty(mBindInfo.getBLSJOpenId())) {
//            Intent intent = HospitalLoginActivity.getIntent(this, HospitalLoginActivity.TYPE_BIND, ThirdPartInfo.TYPE_CREDIT);
//            startActivity(intent);
        }
        // 解绑学分
        else {
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.tips)
//                    .setMessage("确定解除绑定 " + mBindInfo.getBLSJId() + " 吗?")
//                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
//                            thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
//                            Qq qq = new Qq();
//                            qq.setOpenId(mBindInfo.getQQOpenId());
//                            thirdPartInfo.setQQData(qq);
//                            thirdPartInfo.setType(ThirdPartInfo.TYPE_QQ);
//                            unBind(thirdPartInfo);
//                        }
//                    })
//                    .setNegativeButton(R.string.cancel, null)
//                    .show();
        }
    }

    /**
     * 排班
     *
     * @param view
     */
    @OnClick(R.id.rl_schedule)
    void onSchedule(View view) {
        // 绑定排班
        if (TextUtils.isEmpty(mBindInfo.getBLSJOpenId())) {
//            Intent intent = HospitalLoginActivity.getIntent(this, HospitalLoginActivity.TYPE_BIND, ThirdPartInfo.TYPE_SCHEDULE);
//            startActivity(intent);
        }
        // 解绑排班
        else {
//            new AlertDialog.Builder(this)
//                    .setTitle(R.string.tips)
//                    .setMessage("确定解除绑定 " + mBindInfo.getBLSJId() + " 吗?")
//                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
//                            thirdPartInfo.setRegisterId(mBindInfo.getRegisterId());
//                            Qq qq = new Qq();
//                            qq.setOpenId(mBindInfo.getQQOpenId());
//                            thirdPartInfo.setQQData(qq);
//                            thirdPartInfo.setType(ThirdPartInfo.TYPE_QQ);
//                            unBind(thirdPartInfo);
//                        }
//                    })
//                    .setNegativeButton(R.string.cancel, null)
//                    .show();
        }
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
