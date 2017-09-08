package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.FriendListResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.OfficeResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class ContactListActivity extends BaseActivity {

    private static final String EXTRA_OFFICE = "extra_office";

    @Bind(R.id.tb_contact_list)
    TitleBar mTitleBar;

    @Bind(R.id.rv_contact_list)
    RecyclerView mRecyclerView;

    private LoginInfo mLoginInfo;

    private ProgressDialogManager mProgressDialogManager;

    private ContactAdapter mAdapter;

    public static Intent getIntent(Context context, OfficeResult.Office office) {
        Intent intent = new Intent(context, ContactListActivity.class);
        intent.putExtra(EXTRA_OFFICE, office);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_contact_list;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);

        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        String hospitalId = mLoginInfo.getHospitalId();

        OfficeResult.Office office = (OfficeResult.Office) getIntent().getSerializableExtra(EXTRA_OFFICE);

        if (office != null) {
            getContactByOffice(hospitalId, office.getDepartmentId());
            mTitleBar.setTitle(office.getName());
        } else {
            if (mLoginInfo.getDepartmentName() != null) {
                mTitleBar.setTitle(mLoginInfo.getDepartmentName());
            }
            String officeId = mLoginInfo.getDepartmentId();
            if (hospitalId != null && officeId != null) {
                getContactByOffice(hospitalId, officeId);
            }

        }

    }

    /**
     * 获取科室内所有联系人
     */
    private void getContactByOffice(String hospitalId, String officeId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_CONTACT_LIST_BY_OFFICE_ID)
                .addParams("HospitalId", hospitalId)
                .addParams("DepartmentId", officeId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        FriendListResult result = new Gson().fromJson(response, FriendListResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            List<Contact> contacts = result.getBody();
                            MainActivity.updateContactInfoData(contacts);
                            setListData(contacts);
                        } else {
                            T.showShort(ContactListActivity.this, result.getMsg());
                        }
                    }
                });
    }

    private void setListData(List<Contact> contacts) {
        // 设置间隔线
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new ContactAdapter(this, R.layout.item_contact, contacts);
        mRecyclerView.setAdapter(mAdapter);

    }

    class ContactAdapter extends CommonAdapter<Contact> {

        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, final Contact contact, int position) {
            ImageView imageView = holder.getView(R.id.iv_avatar);
            Glide.with(ContactListActivity.this).load(contact.getAvatar())
                    .error(R.drawable.icon_avatar_default).into(imageView);
            holder.setText(R.id.tv_nickname, contact.getTarget());
            holder.getView(R.id.rl_contact).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ContactDetailActivity.getIntent(ContactListActivity.this, contact.getFriendId());
                    startActivity(intent);
                }
            });
        }
    }
}
