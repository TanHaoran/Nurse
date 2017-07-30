package com.jerry.nurse.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.activity.HtmlActivity;
import com.jerry.nurse.activity.PersonalInfoActivity;
import com.jerry.nurse.activity.SettingActivity;
import com.jerry.nurse.model.UserPractisingCertificateInfo;
import com.jerry.nurse.model.UserProfessionalCertificateInfo;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.view.CircleImageView;
import com.jerry.nurse.view.ValidatedView;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;

import static com.jerry.nurse.constant.ServiceConstant.AUDIT_SUCCESS;


/**
 * Created by Jerry on 2017/7/15.
 */

public class MeFragment extends BaseFragment {

    @Bind(R.id.civ_avatar)
    CircleImageView mAvatarImageView;

    @Bind(R.id.tv_name)
    TextView mNameTextView;

    @Bind(R.id.tv_nickname)
    TextView mNicknameTextView;

    @Bind(R.id.vv_valid)
    ValidatedView mValidatedView;

    /**
     * 实例化方法
     *
     * @return
     */
    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_me;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        super.onResume();
        // 初始化用户信息显示
        initUserInfo();
    }

    /**
     * 初始化用户信息显示
     */
    private void initUserInfo() {
        UserRegisterInfo userRegisterInfo = DataSupport.findFirst(UserRegisterInfo.class);

        UserProfessionalCertificateInfo userProfessionalCertificateInfo =
                DataSupport.findFirst(UserProfessionalCertificateInfo.class);

        UserPractisingCertificateInfo userPractisingCertificateInfo =
                DataSupport.findFirst(UserPractisingCertificateInfo.class);

        // TODO 设置头像
        if (!TextUtils.isEmpty(userRegisterInfo.getName())) {
            mNameTextView.setText(userRegisterInfo.getName());
        }
        if (!TextUtils.isEmpty(userRegisterInfo.getNickName())) {
            mNicknameTextView.setText(userRegisterInfo.getNickName());
        }

        if (userProfessionalCertificateInfo != null &&
                userProfessionalCertificateInfo.getVerifyStatus() == AUDIT_SUCCESS &&
                userPractisingCertificateInfo != null &&
                userPractisingCertificateInfo.getVerifyStatus() == AUDIT_SUCCESS) {
            mValidatedView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.ll_personal_info)
    void onPersonalInfo(View view) {
        Intent intent = PersonalInfoActivity.getIntent(getActivity());
        startActivity(intent);
    }

    @OnClick(R.id.rl_event_report)
    void onEventReport(View view) {
        Intent intent = HtmlActivity.getIntent(getActivity(), "");
        startActivity(intent);
    }

    @OnClick(R.id.rl_credit)
    void onCredit(View view) {

    }

    @OnClick(R.id.rl_collection)
    void onCollection(View view) {

    }

    @OnClick(R.id.rl_schedule)
    void onSchedule(View view) {

    }

    @OnClick(R.id.rl_exam)
    void onExam(View view) {

    }

    @OnClick(R.id.rl_setting)
    void onSetting(View view) {
        Intent intent = SettingActivity.getIntent(getActivity());
        startActivity(intent);
    }


}
