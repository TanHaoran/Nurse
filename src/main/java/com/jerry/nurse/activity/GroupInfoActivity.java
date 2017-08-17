package com.jerry.nurse.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.GroupInfoResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.ToggleButton;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class GroupInfoActivity extends BaseActivity {

    public static final String EXTRA_GROUP_ID = "extra_group_id";

    @Bind(R.id.rv_group)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_group_name)
    TextView mGroupNameTextView;

    @Bind(R.id.tb_switch)
    ToggleButton mSwitchButton;

    private ContactAdapter mAdapter;

    private String mRegisterId;
    private String mGroupId;
    private List<Contact> mContacts;

    private ProgressDialogManager mProgressDialogManager;

    private GroupInfo mGroupInfo;

    public static Intent getIntent(Context context, String groupId) {
        Intent intent = new Intent(context, GroupInfoActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_chat_group_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);
        mRegisterId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        getGroupInfo(mRegisterId, mGroupId);

    }

    /**
     * 获取用户信息
     */
    private void getGroupInfo(final String registerId, final String groupId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_GROUP_INFO)
                .addParams("RegisterId", registerId)
                .addParams("GroupId", groupId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        GroupInfoResult result = new Gson().fromJson(response, GroupInfoResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mGroupInfo = result.getBody();
                            mGroupInfo.setHXGroupId(groupId);
                            mContacts = result.getBody().getGroupMemberList();
                            if (mContacts == null) {
                                mContacts = new ArrayList<>();
                            }
                            MainActivity.updateGroupInfoData(mGroupInfo);
                            MainActivity.updateContactInfoData(mContacts);
                            setContactsData(mContacts);
                        } else {
                            T.showShort(GroupInfoActivity.this, result.getMsg());
                        }
                    }
                });
    }

    /**
     * 设置数据
     *
     * @param contacts
     */
    private void setContactsData(List<Contact> contacts) {

        mContacts.add(new Contact());
        mAdapter = new ContactAdapter(this, R.layout.item_group_contact, contacts);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mRecyclerView.setAdapter(mAdapter);

        mGroupNameTextView.setText(mGroupInfo.getHXNickName());
    }

    @OnClick(R.id.rl_group_name)
    void onGroupName(View view) {
        Intent intent = InputActivity.getIntent(this, "群昵称");
        startActivity(intent);
    }

    @OnClick(R.id.rl_group_qr_code)
    void onGroupQrCode(View view) {

    }

    @OnClick(R.id.acb_add_friend)
    void onQuitGroup(View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.tips)
                .setMessage("确定退出群聊吗?")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quitGroup();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * 退出群聊
     */
    private void quitGroup() {

    }


    class ContactAdapter extends CommonAdapter<Contact> {
        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(com.zhy.adapter.recyclerview.base.ViewHolder holder, final Contact contact, int position) {
            if (position == mDatas.size() - 1) {
                holder.setImageResource(R.id.iv_avatar, R.drawable.group_add_contact);
            } else {
                holder.setText(R.id.tv_nickname, contact.getNickName());
                ImageView imageView = holder.getView(R.id.iv_avatar);
                Glide.with(GroupInfoActivity.this).load(contact.getAvatar())
                        .placeholder(R.drawable.icon_avatar_default).into(imageView);
            }

            holder.setOnClickListener(R.id.ll_contact, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ContactDetailActivity.getIntent(GroupInfoActivity.this, contact.getFriendId());
                    startActivity(intent);
                }
            });
        }
    }
}
