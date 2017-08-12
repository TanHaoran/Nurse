package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.Message;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.RecycleViewDivider;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

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

        // 设置间隔线
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));

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
            switch (status) {
                case AddFriendApply.STATUS_SEND_ING:
                    holder.setVisible(R.id.acb_agree, false);
                    holder.setVisible(R.id.acb_refuse, false);
                    holder.setVisible(R.id.tv_result, true);
                    holder.setVisible(R.id.tv_reason, false);
                    holder.setText(R.id.tv_result, "已发出申请");
                    break;
                case AddFriendApply.STATUS_REFUSE:
                    holder.setVisible(R.id.acb_agree, false);
                    holder.setVisible(R.id.acb_refuse, false);
                    holder.setVisible(R.id.tv_reason, false);
                    holder.setVisible(R.id.tv_result, true);
                    holder.setText(R.id.tv_result, "已拒绝");
                    break;
                case AddFriendApply.STATUS_AGREE:
                    holder.setVisible(R.id.acb_agree, false);
                    holder.setVisible(R.id.acb_refuse, false);
                    holder.setVisible(R.id.tv_reason, false);
                    holder.setVisible(R.id.tv_result, true);
                    holder.setText(R.id.tv_result, "已同意");
                    break;
                case AddFriendApply.STATUS_RECEIVE_ING:
                    holder.setVisible(R.id.acb_agree, true);
                    holder.setVisible(R.id.acb_refuse, true);
                    holder.setVisible(R.id.tv_result, false);
                    holder.setVisible(R.id.tv_reason, true);
                    holder.setText(R.id.tv_reason, apply.getReason());
                    break;
            }
            holder.setText(R.id.tv_nickname, apply.getNickname());
            ImageView imageView = holder.getView(R.id.iv_avatar);

            // 点击同意好友申请
            holder.setOnClickListener(R.id.acb_agree, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(apply.getContactId());
                        holder.setVisible(R.id.acb_agree, false);
                        holder.setVisible(R.id.acb_refuse, false);
                        holder.setVisible(R.id.tv_result, true);
                        holder.setText(R.id.tv_result, "已同意");
                        updateLocalData(apply, true);
                        addAsFriend(mRegisterId, apply.getContactId());
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
                        holder.setVisible(R.id.acb_agree, false);
                        holder.setVisible(R.id.acb_refuse, false);
                        holder.setVisible(R.id.tv_result, true);
                        holder.setText(R.id.tv_result, "已拒绝");
                        updateLocalData(apply, false);
                    } catch (HyphenateException e) {
                        L.i("拒绝好友申请失败");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 更新本地数据库
     *
     * @param a
     * @param agree 是否同意
     */
    private void updateLocalData(AddFriendApply a, boolean agree) {
        // 构建首页消息
        Message message = null;
        try {
            message = DataSupport.where("mType=? and mRegisterId=?", "0", mRegisterId).findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setType(Message.TYPE_ADD_FRIEND_APPLY);
        message.setImageResource(R.drawable.icon_pb);
        message.setTitle("好友申请");
        message.setTime(new Date().getTime());
        message.setRegisterId(mRegisterId);
        message.setContactId(a.getContactId());
        if (agree) {
            message.setContent("已同意" + a.getNickname() + "的申请");
        } else {
            message.setContent("已拒绝" + a.getNickname() + "的申请");
        }
        message.save();

        // 构建添加好友消息
        AddFriendApply apply = null;
        try {
            apply = DataSupport.where("mRegisterId=? and mContactId=?",
                    mRegisterId, a.getContactId()).findFirst(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apply == null) {
            apply = new AddFriendApply();
        }
        apply.setAvatar(a.getAvatar());
        apply.setNickname(a.getNickname());
        apply.setContactId(a.getContactId());
        apply.setRegisterId(mRegisterId);
        if (agree) {
            apply.setStatus(AddFriendApply.STATUS_AGREE);
        } else {
            apply.setStatus(AddFriendApply.STATUS_REFUSE);
        }
        apply.setTime(new Date().getTime());
        apply.save();
    }

    private void addAsFriend(final String myId, final String friendId) {
        OkHttpUtils.get().url(ServiceConstant.ADD_AS_FRIEND)
                .addParams("MyId", myId)
                .addParams("FriendId", friendId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("添加好友成功");
                            T.showShort(AddContactApplyActivity.this, "添加好友成功");
                        } else {
                            T.showShort(AddContactApplyActivity.this, commonResult.getMsg());
                            L.i("添加好友失败" + commonResult.getMsg());
                        }
                    }
                });
    }
}
