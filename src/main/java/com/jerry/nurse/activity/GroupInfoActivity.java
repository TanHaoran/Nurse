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
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.GroupInfoResult;
import com.jerry.nurse.model.Message;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.jerry.nurse.view.ToggleButton;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;
import static org.litepal.crud.DataSupport.where;

public class GroupInfoActivity extends BaseActivity {


    private static final int REQUEST_UPDATE_NICKNAME = 0x102;
    private static final int REQUEST_ADD_GROUP_MEMBER = 0x103;

    public static final String EXTRA_GROUP_ID = "extra_group_id";

    public static final String EXTRA_GROUP_NICKNAME = "extra_group_nickname";

    @Bind(R.id.tb_group)
    TitleBar mTitleBar;

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
     * 获取群信息
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
                            mTitleBar.setTitle(result.getBody().getHXNickName());
                            mGroupInfo = result.getBody();
                            mGroupInfo.setGroupId(groupId);
                            mContacts = result.getBody().getGroupMemberList();
                            if (mContacts == null) {
                                mContacts = new ArrayList<>();
                            }
                            MainActivity.updateGroupInfoData(mGroupInfo);
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
        mTitleBar.setTitle(mGroupInfo.getHXNickName());
    }

    @OnClick(R.id.rl_group_name)
    void onGroupName(View view) {
        Intent intent = InputActivity.getIntent(this, "群昵称", mGroupInfo.getHXNickName());
        startActivityForResult(intent, REQUEST_UPDATE_NICKNAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 修改群昵称
            if (requestCode == REQUEST_UPDATE_NICKNAME) {
                String nickname = data.getStringExtra(EXTRA_GROUP_NICKNAME);
                postGroupNickname(nickname);
            } else if (requestCode == REQUEST_ADD_GROUP_MEMBER) {
                getGroupInfo(mRegisterId, mGroupId);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 修改群昵称
     *
     * @param nickname
     */
    void postGroupNickname(final String nickname) {
        mProgressDialogManager.show();
        mGroupInfo.setHXNickName(nickname);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_GROUP_NICKNAME)
                .content(StringUtil.addModelWithJson(mGroupInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            T.showShort(GroupInfoActivity.this, "群昵称设置成功");
                            mGroupInfo.getGroupMemberList().remove(mGroupInfo.getGroupMemberList().size() - 1);
                            MainActivity.updateGroupInfoData(mGroupInfo);
                            // 发送一条修改群昵称的消息
                            sendChangeGroupNicknameMessage(mGroupInfo.getHXGroupId(), mGroupInfo.getHXNickName());
                            // 更新界面
                            setContactsData(mGroupInfo.getGroupMemberList());
                        } else {
                            T.showShort(GroupInfoActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }

    /**
     * 发送修改群昵称的消息
     *
     * @param hxGroupId
     * @param hxNickName
     */
    private void sendChangeGroupNicknameMessage(String hxGroupId, String hxNickName) {
        EMMessage message = EMMessage.createTxtSendMessage("我修改了群昵称为 " + hxNickName, hxGroupId);
        //如果是群聊，设置chattype，默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @OnClick(R.id.rl_group_qr_code)
    void onGroupQrCode(View view) {

    }

    @OnClick(R.id.acb_quit_group)
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
        mGroupInfo.setRegisterId(mRegisterId);
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.QUIT_GROUP)
                .content(StringUtil.addModelWithJson(mGroupInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            // 删除数据库中群信息
                            GroupInfo info = where("HXGroupId=?",
                                    mGroupInfo.getHXGroupId()).findFirst(GroupInfo.class);
                            info.delete();
                            // 删除消息列表中的群聊天记录
                            List<Message> msgs = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                                    mRegisterId, mGroupInfo.getHXGroupId(), "2").find(Message.class);
                            for (Message m : msgs) {
                                m.delete();
                            }
                            T.showShort(GroupInfoActivity.this, "你已退出该群聊");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            T.showShort(GroupInfoActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }

    class ContactAdapter extends CommonAdapter<Contact> {
        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(com.zhy.adapter.recyclerview.base.ViewHolder holder, final Contact contact, final int position) {
            if (position == mDatas.size() - 1) {
                holder.setImageResource(R.id.iv_avatar, R.drawable.group_add_contact);
            } else {
                holder.setText(R.id.tv_nickname, contact.getTarget());
                ImageView imageView = holder.getView(R.id.iv_avatar);
                Glide.with(GroupInfoActivity.this).load(contact.getAvatar())
                        .placeholder(R.drawable.icon_avatar_default).into(imageView);
            }

            holder.setOnClickListener(R.id.rl_contact, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 最后一个是添加群成员
                    if (position != mDatas.size() - 1) {
                        Intent intent = ContactDetailActivity.getIntent(GroupInfoActivity.this, contact.getFriendId());
                        startActivity(intent);
                    } else {
                        Intent intent = CreateGroupActivity.getIntent(GroupInfoActivity.this,
                                mGroupInfo.getHXGroupId(), mContacts);
                        startActivityForResult(intent, REQUEST_ADD_GROUP_MEMBER);
                    }
                }
            });
        }
    }
}
