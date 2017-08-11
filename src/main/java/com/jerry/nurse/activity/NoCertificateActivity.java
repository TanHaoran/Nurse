package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.model.UserInfo;
import com.jerry.nurse.view.TitleBar;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;

import static com.jerry.nurse.constant.ServiceConstant.AUDIT_EMPTY;

public class NoCertificateActivity extends BaseActivity {

    public static final String EXTRA_TITLE = "title";

    public static final String PROFESSIONAL_CERTIFICATE = "专业技术资格证书";
    public static final String PRACTISING_CERTIFICATE = "护士执业证书";

    @Bind(R.id.tb_certificate)
    TitleBar mTitleBar;

    private String mTitle;

    public static Intent getIntent(Context context, String title) {
        Intent intent = new Intent(context, NoCertificateActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_no_certificate;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        mTitleBar.setTitle(mTitle);
        UserInfo userInfo = DataSupport.findFirst(UserInfo.class);
        if (PROFESSIONAL_CERTIFICATE.equals(mTitle)) {
            if (userInfo.getQVerifyStatus() != AUDIT_EMPTY) {
                Intent intent = ProfessionalCertificateActivity.getIntent(this);
                startActivity(intent);
                finish();
            }
        } else if (PRACTISING_CERTIFICATE.equals(mTitle)) {
            if (userInfo.getPVerifyStatus() != AUDIT_EMPTY) {
                Intent intent = PractisingCertificateActivity.getIntent(this);
                startActivity(intent);
                finish();
            }
        }
    }

    @OnClick(R.id.acb_go_certificate)
    void onGoCertificateButton(View view) {
        if (PROFESSIONAL_CERTIFICATE.equals(mTitle)) {
            Intent intent = ProfessionalCertificateActivity.getIntent(this);
            startActivity(intent);
            finish();
        } else if (PRACTISING_CERTIFICATE.equals(mTitle)) {
            Intent intent = PractisingCertificateActivity.getIntent(this);
            startActivity(intent);
            finish();
        }
    }
}
