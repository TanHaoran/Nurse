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
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LocalContactCache;
import com.jerry.nurse.util.MessageManager;
import com.jerry.nurse.util.RecyclerViewDecorationUtil;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;
import static com.jerry.nurse.model.AddFriendApply.STATUS_AGREE;

public class AddContactApplyActivity extends BaseActivity {

    @Bind(R.id.rv_apply)
    RecyclerView mRecyclerView;

    private ApplyAdapter mAdapter;

    private List<AddFriendApply> mApplies;
    private String mRegisterId;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, AddContactApplyActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_add_friend_apply;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        mRegisterId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        // 加载本地数据库中的消息
        loadLocalMessage();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewDecorationUtil.addItemDecoration(this, mRecyclerView);
        mAdapter = new ApplyAdapter(this, R.layout.item_add_friend_apply, mApplies);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 加载本地数据库
     */
    private void loadLocalMessage() {
        try {
            mApplies = DataSupport.where("mRegisterId=?", mRegisterId).order("mTime desc").find(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mApplies == null) {
            mApplies = new ArrayList<>();
        }
    }

    class ApplyAdapter extends CommonAdapter<AddFriendApply> {

        public ApplyAdapter(Context context, int layoutId, List<AddFriendApply> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(final ViewHolder holder, final AddFriendApply apply, final int position) {
            int status = apply.getStatus();
            // 控制几个按钮的状态
            switch (status) {
                // 已发送好友申请
                case AddFriendApply.STATUS_SEND_ING:
                    setDisplayStatus(holder, AddFriendApply.STATUS_SEND_ING);
                    break;
                // 已拒绝
                case AddFriendApply.STATUS_REFUSE:
                    setDisplayStatus(holder, AddFriendApply.STATUS_REFUSE);
                    break;
                // 已同意
                case STATUS_AGREE:
                    setDisplayStatus(holder, STATUS_AGREE);
                    break;
                // 收到好友邀请
                case AddFriendApply.STATUS_RECEIVE_ING:
                    setDisplayStatus(holder, AddFriendApply.STATUS_RECEIVE_ING);
                    break;
            }

            new LocalContactCache() {
                @Override
                protected void onLoadContactInfoSuccess(ContactInfo info) {
                    // 设置昵称位置显示内容
                    holder.setText(R.id.tv_title, info.getDisplayName());
                    holder.setText(R.id.tv_content, "验证信息:" + apply.getReason());
                    ImageView imageView = holder.getView(R.id.iv_avatar_arrow);
                    Glide.with(AddContactApplyActivity.this).load(apply.getAvatar())
                            .error(R.drawable.icon_avatar_default).into(imageView);
                }
            }.getContactInfo(mRegisterId, apply.getContactId());

            // 点击同意好友申请
            holder.setOnClickListener(R.id.acb_agree, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(apply.getContactId());
                        setDisplayStatus(holder, AddFriendApply
                                .STATUS_AGREE);

                        new LocalContactCache() {
                            @Override
                            protected void onLoadContactInfoSuccess(ContactInfo info) {
                                info.setFriend(true);
                                info.save();
                                // 更新数据库
                                MessageManager.updateApplyData(apply, true, info);
                                addAsFriend(mRegisterId, apply.getContactId());
                            }
                        }.getContactInfo(EMClient.getInstance().getCurrentUser(),
                                apply.getContactId());

                    } catch (HyphenateException e) {
                        L.i("同意好友申请失败");
                        e.printStackTrace();
                    }
                }
            });

            // 点击拒绝申请
            holder.setOnClickListener(R.id.acb_refuse, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(apply.getContactId());
                        setDisplayStatus(holder, AddFriendApply
                                .STATUS_REFUSE);

                        new LocalContactCache() {
                            @Override
                            protected void onLoadContactInfoSuccess(ContactInfo info) {
                                // 更新数据库
                                MessageManager.updateApplyData(apply, false, info);
                            }
                        }.getContactInfo(EMClient.getInstance().getCurrentUser(),
                                apply.getContactId());

                    } catch (HyphenateException e) {
                        L.i("拒绝好友申请失败");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 设置几种不同的按钮显示状态
     *
     * @param holder
     * @param status
     */
    private void setDisplayStatus(ViewHolder holder, int status) {
        switch (status) {
            case AddFriendApply.STATUS_SEND_ING:
                holder.setVisible(R.id.acb_agree, false);
                holder.setVisible(R.id.acb_refuse, false);
                holder.setVisible(R.id.tv_result, true);
                holder.setText(R.id.tv_result, "已发出申请");
                break;
            case AddFriendApply.STATUS_REFUSE:
                holder.setVisible(R.id.acb_agree, false);
                holder.setVisible(R.id.acb_refuse, false);
                holder.setVisible(R.id.tv_result, true);
                holder.setText(R.id.tv_result, "已拒绝");
                break;
            case STATUS_AGREE:
                holder.setVisible(R.id.acb_agree, false);
                holder.setVisible(R.id.acb_refuse, false);
                holder.setVisible(R.id.tv_result, true);
                holder.setText(R.id.tv_result, "已同意");
                break;
            case AddFriendApply.STATUS_RECEIVE_ING:
                holder.setVisible(R.id.acb_agree, true);
                holder.setVisible(R.id.acb_refuse, true);
                holder.setVisible(R.id.tv_result, false);
                break;
            default:
                break;
        }
    }

    /**
     * 向服务器发送两个人成为好友的状态
     *
     * @param myId
     * @param friendId
     */
    private void addAsFriend(final String myId, final String friendId) {
        OkHttpUtils.get().url(ServiceConstant.ADD_AS_FRIEND)
                .addParams("MyId", myId)
                .addParams("FriendId", friendId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult result = new Gson().fromJson(response,
                                CommonResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            T.showShort(AddContactApplyActivity.this, "添加好友成功");
                        } else {
                            T.showShort(AddContactApplyActivity.this, result.getMsg());
                        }
                    }
                });
    }
}
