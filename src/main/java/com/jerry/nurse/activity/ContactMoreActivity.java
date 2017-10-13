package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class ContactMoreActivity extends BaseActivity {

    public static final String EXTRA_CONTACT = "extra_contact";
    public static final String EXTRA_REMARK = "extra_remark";

    private static final int REQUEST_REMARK = 0x101;

    @Bind(R.id.tv_remark)
    TextView mRemarkTextView;

    private Contact mContact;

    public static Intent getIntent(Context context, Contact contact) {
        Intent intent = new Intent(context, ContactMoreActivity.class);
        intent.putExtra(EXTRA_CONTACT, contact);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_contact_more;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mContact = (Contact) getIntent().getSerializableExtra(EXTRA_CONTACT);
        mRemarkTextView.setText(mContact.getRemark());
    }


    @OnClick(R.id.rl_remark)
    void onRemark(View view) {
        Intent intent = InputActivity.getIntent(this, InputActivity.REMARK, mContact.getRemark());
        startActivityForResult(intent, REQUEST_REMARK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_REMARK) {
                String remark = data.getStringExtra(EXTRA_REMARK);
                postRemark(remark);
            }
        }
    }

    /**
     * 修改备注
     *
     * @param remark
     */
    private void postRemark(final String remark) {
        mContact.setRemark(remark);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_CONTACT_REMARK)
                .content(StringUtil.addModelWithJson(mContact))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("设置备注成功");
                            // 更新数据库
                            mContact.setRemark(remark);
                            mRemarkTextView.setText(mContact.getRemark());
                            MainActivity.updateContactInfoData(mContact);
                        } else {
                            L.i("设置备注失败");
                            T.showShort(ContactMoreActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }
}
