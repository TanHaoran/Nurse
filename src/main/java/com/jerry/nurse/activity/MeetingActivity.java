package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.HospitalResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.UserInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.AccountValidatorUtil;
import com.jerry.nurse.util.BottomDialogManager;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LitePalUtil;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.ClearEditText;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class MeetingActivity extends BaseActivity {

    @Bind(R.id.tb_meeting)
    TitleBar mTitleBar;

    @Bind(R.id.tv_hospital)
    TextView mHospitalTextView;

    @Bind(R.id.tv_duty)
    TextView mDutyView;

    @Bind(R.id.tv_role)
    TextView mRoleView;

    @Bind(R.id.cet_name)
    ClearEditText mEditTextName;

    @Bind(R.id.cet_cellphone)
    ClearEditText mEditTextCellphone;

    private List<HospitalResult.Hospital> mHospitals;
    private List<String> mHospitalNames;

    private HospitalResult.Hospital mHospital;

    private String mDuty;
    private int mRole = -1;

    // 用户登录信息
    private LoginInfo mLoginInfo;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MeetingActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_meeting;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // 入口处判断用户是否已经登录，如果已经登录直接跳转到主界面
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        if (mLoginInfo != null) {
            if (mLoginInfo.getHospitalId() != null) {
                goToMainActivity();
            } else{
                getHospitals(0, 0);
            }
        } else{
            getHospitals(0, 0);
        }

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                if (checkValidate()) {
                    meetingComplete(mLoginInfo.getRegisterId(), mHospital.getHospitalId(),
                            mDuty, mRole, mEditTextName.getText().toString(),
                            mEditTextCellphone.getText().toString());
                }
            }
        });
    }

    /**
     * 检测表单是否合法
     *
     * @return
     */
    private boolean checkValidate() {
        boolean validate = true;

        if (mHospital == null) {
            validate = false;
            T.showShort(this, "请选择医院");
            return validate;
        }

        if (TextUtils.isEmpty(mEditTextName.getText().toString())) {
            validate = false;
            T.showShort(this, "请填写姓名");
            return validate;
        }

        if (TextUtils.isEmpty(mEditTextCellphone.getText().toString())) {
            validate = false;
            T.showShort(this, "请填写手机号");
            return validate;
        }

        if (!AccountValidatorUtil.isMobile(mEditTextCellphone.getText().toString())) {
            validate = false;
            T.showShort(this, "手机格式有误");
            return validate;
        }

        return validate;
    }

    @OnClick(R.id.rl_hospital)
    void onHospital(View view) {

        // 创建并显示底布弹出选择框
        BottomDialogManager dialog = new BottomDialogManager(MeetingActivity.this);
        dialog.setOnItemSelectedListener(mHospitalNames, new BottomDialogManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, String item) {
                mHospitalTextView.setText(item);
                mHospital = mHospitals.get(position);
                L.i("选择的医院Id是:" + mHospital.getHospitalId());
            }
        });
        dialog.showSelectDialog(mHospitalTextView.getText().toString());
        dialog.setTitle("请选择医院");
    }

    @OnClick(R.id.rl_duty)
    void onDuty(View view) {
        // 创建并显示底布弹出选择框
        final String[] duties = {"护士", "护师", "护士长", "总护士长", "其他"};
        BottomDialogManager dialog = new BottomDialogManager(MeetingActivity.this);
        dialog.setOnItemSelectedListener(Arrays.asList(duties), new BottomDialogManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, String item) {
                mDutyView.setText(item);
                mDuty = duties[position];
            }
        });
        dialog.showSelectDialog(mHospitalTextView.getText().toString());
        dialog.setTitle("请选择职务");
    }

    @OnClick(R.id.rl_role)
    void onRole(View view) {  // 创建并显示底布弹出选择框
        final String[] roles = {"上报事件", "审核事件"};
        BottomDialogManager dialog = new BottomDialogManager(MeetingActivity.this);
        dialog.setOnItemSelectedListener(Arrays.asList(roles), new BottomDialogManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, String item) {
                mRoleView.setText(item);
                mRole = position;
            }
        });
        dialog.showSelectDialog(mHospitalTextView.getText().toString());
        dialog.setTitle("请选择角色");
    }


    /**
     * 获取所有医院信息
     *
     * @param latitude
     * @param longitude
     */
    private void getHospitals(double latitude, double longitude) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_NEARBY_HOSPITAL_LIST)
                .addParams("lat", String.valueOf(latitude))
                .addParams("lng", String.valueOf(longitude))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        HospitalResult result = new Gson().fromJson(response, HospitalResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mHospitals = result.getBody();
                            if (mHospitals == null) {
                                T.showShort(MeetingActivity.this, "医院列表为空");
                                return;
                            } else {
                                mHospitalNames = new ArrayList<>();
                                for (HospitalResult.Hospital h : mHospitals) {
                                    mHospitalNames.add(h.getName());
                                }
                            }
                        }
                    }
                });
    }


    /**
     * 会议完善信息
     *
     * @param registerId
     * @param hospitalId
     * @param duty
     * @param role
     * @param name
     * @param cellphone
     */
    private void meetingComplete(final String registerId, final String hospitalId, String duty, final int role, String name, String cellphone) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.MEETING_COMPLETE)
                .addParams("registerId", registerId)
                .addParams("hospitalId", hospitalId)
                .addParams("duty", "护士")
                .addParams("role", "0")
                .addParams("name", name)
                .addParams("phone", cellphone)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        HospitalResult result = new Gson().fromJson(response, HospitalResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            // 更新个人医院信息、并跳转到主页
                            // 更新数据库
                            mLoginInfo.setHospitalId(hospitalId);
                            LitePalUtil.updateLoginInfo(MeetingActivity.this, mLoginInfo);

                            UserInfo userInfo = DataSupport.findFirst(UserInfo.class);
                            if (userInfo == null) {
                                userInfo = new UserInfo();
                            }
                            userInfo.setRegisterId(registerId);
                            userInfo.setHospitalId(hospitalId);
                            LitePalUtil.updateUserInfo(MeetingActivity.this, userInfo);

                            SPUtil.put(MeetingActivity.this, SPUtil.ROLE, role);

                            Intent intent = MainActivity.getIntent(MeetingActivity.this);
                            startActivity(intent);
                        }
                    }
                });
    }

    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        Intent intent = MainActivity.getIntent(MeetingActivity.this);
        startActivity(intent);
        finish();
    }
}
