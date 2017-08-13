package com.jerry.nurse.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactDetailResult;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.MessageManager;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class ContactDetailActivity extends BaseActivity {

    private static final int REQUEST_ADD_FRIEND = 0x101;

    public static final String EXTRA_VALIDATE_MESSAGE = "extra_validate_message";

    @Bind(R.id.ll_contact_option)
    LinearLayout mOptionLayout;

    @Bind(R.id.iv_avatar)
    ImageView mAvatarImageView;

    @Bind(R.id.tv_name)
    TextView mNameTextView;

    @Bind(R.id.tv_remark)
    TextView mRemarkTextView;

    @Bind(R.id.tv_nickname)
    TextView mNicknameTextView;

    @Bind(R.id.tv_sex)
    TextView mSexTextView;

    @Bind(R.id.tv_cellphone)
    TextView mCellphoneTextView;

    @Bind(R.id.tv_hospital)
    TextView mHospitalTextView;

    @Bind(R.id.tv_office)
    TextView mOfficeTextView;

    @Bind(R.id.acb_add_friend)
    AppCompatButton mAddFriendButton;

    @Bind(R.id.ll_hospital)
    LinearLayout mHospitalLayout;

    @Bind(R.id.ll_office)
    LinearLayout mOfficeLayout;

    private ProgressDialogManager mProgressDialogManager;

    public static final String EXTRA_REGISTER_ID = "extra_register_id";

    private String mRegisterId;
    private String mToAddRegisterId;

    private Contact mContact;

    public static Intent getIntent(Context context, String registerId) {
        Intent intent = new Intent(context, ContactDetailActivity.class);
        intent.putExtra(EXTRA_REGISTER_ID, registerId);
        return intent;
    }


    @Override
    public int getContentViewResId() {
        return R.layout.activity_contact_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);
        mRegisterId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        mToAddRegisterId = getIntent().getStringExtra(EXTRA_REGISTER_ID);
        getUserDetail(mRegisterId, mToAddRegisterId);
    }

    /**
     * 查询用户资料
     *
     * @param registerId
     * @param userRegisterId
     */
    private void getUserDetail(String registerId, String userRegisterId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_USER_DETAIL_INFO)
                .addParams("MyId", registerId)
                .addParams("FriendId", userRegisterId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        ContactDetailResult contactDetailResult = new Gson().fromJson(response, ContactDetailResult.class);
                        if (contactDetailResult.getCode() == RESPONSE_SUCCESS) {
                            if (contactDetailResult.getBody() != null) {
                                mContact = contactDetailResult.getBody();
                                ContactInfo info = new ContactInfo();
                                info.setAvatar(mContact.getAvatar());
                                info.setName(mContact.getName());
                                info.setNickName(mContact.getNickName());
                                info.setCellphone(mContact.getPhone());
                                info.setRemark(mContact.getRemark());
                                info.setRegisterId(mContact.getFriendId());
                                info.save();
                                setUserData(mContact);
                            }
                        } else {
                            T.showShort(ContactDetailActivity.this, contactDetailResult.getMsg());
                        }
                    }
                });
    }

    /**
     * 设置用户资料
     *
     * @param contact
     */
    private void setUserData(Contact contact) {
        L.i("联系人信息是：" + contact.toString());
        // 如果不是好友
        if (!contact.isIsFriend()) {
            mOptionLayout.setVisibility(View.GONE);
            mHospitalLayout.setVisibility(View.GONE);
            mOfficeLayout.setVisibility(View.GONE);
            mNameTextView.setVisibility(View.INVISIBLE);
            String cellphone = contact.getPhone().substring(0, 2) + "*******" + contact.getPhone().substring(9);
            mCellphoneTextView.setText(cellphone);
        } else {
            mAddFriendButton.setText("删除好友");
            mCellphoneTextView.setText(contact.getPhone());
        }
        // 设置头像
        Glide.with(this).load(contact.getAvatar()).into(mAvatarImageView);
        mNameTextView.setText(contact.getName());
        mNicknameTextView.setText(contact.getNickName());
        mRemarkTextView.setText(contact.getRemark());
        mSexTextView.setText(contact.getSex());
        mHospitalTextView.setText(contact.getHospitalName());
        mOfficeTextView.setText(contact.getDepartmentName());
    }


    @OnClick(R.id.acb_call)
    void onCall(View view) {

    }

    @OnClick(R.id.acb_send_message)
    void onSendMessage() {
        Intent intent = ChatActivity.getIntent(this, mContact.getFriendId());
        startActivity(intent);
    }

    @OnClick(R.id.acb_add_friend)
    void onAddFriend(View view) {
        if (!mContact.isIsFriend()) {
            Intent intent = InputActivity.getIntent(this, "验证消息");
            startActivityForResult(intent, REQUEST_ADD_FRIEND);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.tips)
                    .setMessage("确定删除好友吗?")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteFriend();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        }
    }

    /**
     * 删除好友
     */
    private void deleteFriend() {
        try {
            EMClient.getInstance().contactManager().deleteContact(mContact.getFriendId());
            OkHttpUtils.get().url(ServiceConstant.DELETE_FRIEND)
                    .addParams("MyId", mRegisterId)
                    .addParams("FriendId", mContact.getFriendId())
                    .build()
                    .execute(new FilterStringCallback() {

                        @Override
                        public void onFilterResponse(String response, int id) {
                            ContactInfo info = DataSupport.where("mRegisterId=?",
                                    mContact.getFriendId()).findFirst(ContactInfo.class);
                            info.delete();
                            ContactDetailResult contactDetailResult = new Gson().fromJson(response, ContactDetailResult.class);
                            if (contactDetailResult.getCode() == RESPONSE_SUCCESS) {
                                if (contactDetailResult.getBody() != null) {
                                    T.showShort(ContactDetailActivity.this, "删除好友成功");
                                }
                            } else {
                                T.showShort(ContactDetailActivity.this, contactDetailResult.getMsg());
                            }
                        }
                    });
            finish();
        } catch (HyphenateException e) {
            L.i("删除好友失败");
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_ADD_FRIEND) {
            String reason = data.getStringExtra(EXTRA_VALIDATE_MESSAGE);
            // 发送好友申请
            sendAddFriendApply(reason);
        }
    }

    /**
     * 发送好友申请
     *
     * @param reason
     */
    private void sendAddFriendApply(String reason) {
        //参数为要添加的好友的username和添加理由
        try {
            EMClient.getInstance().contactManager().addContact(mToAddRegisterId, reason);
            T.showShort(this, "已发送好友申请");
            MessageManager.saveSendAddFriendApplyLocalData(mToAddRegisterId, reason);

        } catch (HyphenateException e) {
            T.showShort(this, "发送申请失败");
            e.printStackTrace();
        }
    }
}
