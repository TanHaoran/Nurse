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
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.view.RecycleViewDivider;
import com.jerry.nurse.view.TitleBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;

import static com.jerry.nurse.constant.ServiceConstant.AVATAR_ADDRESS;

public class ContactListActivity extends BaseActivity {

    @Bind(R.id.tb_contact_list)
    TitleBar mTitleBar;

    @Bind(R.id.rv_contact_list)
    RecyclerView mRecyclerView;

    private LoginInfo mLoginInfo;

    private ProgressDialogManager mProgressDialogManager;

    private List<Contact> mContacts;

    private ContactAdapter mAdapter;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ContactListActivity.class);
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

        if (mLoginInfo.getDepartmentName() != null) {
            mTitleBar.setTitle(mLoginInfo.getDepartmentName());
        }
        String hospitalId = mLoginInfo.getHospitalId();
        String officeId = mLoginInfo.getDepartmentId();
        if (hospitalId != null && officeId != null) {
            getContactByOffice(hospitalId, officeId);
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
                        try {
                            mContacts = new Gson().fromJson(response,
                                    new TypeToken<List<Contact>>() {
                                    }.getType());
                            setListData();
                        } catch (JsonSyntaxException e) {
                            L.i("获取科室内联系人信息失败");
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void setListData() {
        // 设置间隔线
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));

        mAdapter = new ContactAdapter(this, R.layout.item_contact, mContacts);
        mRecyclerView.setAdapter(mAdapter);

    }

    class ContactAdapter extends CommonAdapter<Contact> {

        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, final Contact contact, int position) {
            holder.setText(R.id.tv_nickname, contact.getTarget());
            ImageView imageView = holder.getView(R.id.iv_avatar);
            if (contact.getAvatar().startsWith("http")) {
                Glide.with(ContactListActivity.this).load(contact.getAvatar()).into(imageView);
            } else {
                Glide.with(ContactListActivity.this).load(AVATAR_ADDRESS + contact.getAvatar()).into(imageView);
            }
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
