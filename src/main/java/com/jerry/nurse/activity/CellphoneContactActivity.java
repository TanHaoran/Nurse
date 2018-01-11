package com.jerry.nurse.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.model.CellphoneContactResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.CellphoneContact;
import com.jerry.nurse.util.CellphoneContactUtil;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.ViewHolder;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class CellphoneContactActivity extends BaseActivity {

    @Bind(R.id.rv_contact)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_hint)
    TextView mHintTextView;

    @Bind(R.id.ib_index)
    IndexBar mIndexBar;

    //设置给IndexBar、ItemDecoration的完整数据集
    private List<BaseIndexPinyinBean> mIndexes;

    //主体部分数据源（城联系人据）
    private List<CellphoneContact> mCellphoneContacts = new ArrayList<>();

    private CellphoneContactAdapter mAdapter;

    private SuspensionDecoration mDecoration;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CellphoneContactActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_cellphone_contact;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        CellphoneContact me = new CellphoneContact();
        me.setRegisterId(registerId);
        // 读取手机通讯录联系人
        List<CellphoneContact> cellphoneContacts = CellphoneContactUtil.getPhoneNumberFromMobile(this);
        // 拼凑需要给服务器传递的联系人
        List<CellphoneContact> ccs = new ArrayList<>();
        ccs.add(me);
        ccs.addAll(cellphoneContacts);
        postCellphoneContact(ccs);
    }

    /**
     * 获取手机联系人中注册情况
     *
     * @param ccs
     */
    private void postCellphoneContact(List<CellphoneContact> ccs) {
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.GET_CELLPHONE_CONTACT)
                .content(StringUtil.addModelWithJson(ccs))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CellphoneContactResult result = new Gson().fromJson(response, CellphoneContactResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mCellphoneContacts = result.getBody();
                            updateView();
                        } else {
                            T.showShort(CellphoneContactActivity.this,
                                    result.getMsg());
                        }
                    }
                });
    }

    /**
     * 填充数据
     */
    private void updateView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mIndexes = new ArrayList<>();

        mAdapter = new CellphoneContactAdapter(this, R.layout.item_cellphone_contact, mCellphoneContacts);

        mRecyclerView.setAdapter(mAdapter);

        mDecoration = new SuspensionDecoration(this, mIndexes);
        mRecyclerView.addItemDecoration(mDecoration);

        // 设置点击显示字母和需要真实的索引
        mIndexBar.setPressedShowTextView(mHintTextView)
                .setNeedRealIndex(true)
                .setLayoutManager(manager);

        // 组织整理数据
        initDatas();
    }


    /**
     * 组织数据源
     *
     * @return
     */
    private void initDatas() {
        //先排序
        mIndexBar.getDataHelper().sortSourceDatas(mCellphoneContacts);

        mAdapter.setDatas(mCellphoneContacts);
        mIndexes.addAll(mCellphoneContacts);

        //设置数据
        mIndexBar.setSourceDatas(mIndexes).invalidate();
        mDecoration.setDatas(mIndexes);

        mAdapter.notifyDataSetChanged();
    }

    class CellphoneContactAdapter extends CommonAdapter<CellphoneContact> {
        public CellphoneContactAdapter(Context context, int layoutId, List<CellphoneContact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final CellphoneContact cellphoneContact) {
            String tag = cellphoneContact.getBaseIndexTag();
            CellphoneContact lastContact = (CellphoneContact) mIndexBar.getDataHelper()
                    .getLast(tag);
            // 判断是否是该字母下最后一个元素从而影响到是否绘制最后一条线
            if (lastContact.getPhone().equals(cellphoneContact.getPhone())) {
                holder.setVisible(R.id.v_divider, false);
            } else {
                holder.setVisible(R.id.v_divider, true);
            }

            ImageView imageView = holder.getView(R.id.iv_avatar);
            Glide.with(CellphoneContactActivity.this).load(cellphoneContact.getAvatar())
                    .placeholder(R.drawable.icon_avatar_default).into(imageView);

            // TODO 这里需要做一下排序，使用我们软件的放在最前列，然后按照字幕排序

            // 没有使用我们的软件-0
            if (cellphoneContact.getStatus() == CellphoneContact.TYPE_NOT_USAGE) {
                holder.setVisible(R.id.tv_is_friend, false);
                holder.setVisible(R.id.acb_add, false);
                holder.setVisible(R.id.acb_invite, true);
            }
            // 使用我们的软件，但不是好友-1
            else if (cellphoneContact.getStatus() == CellphoneContact.TYPE_NOT_FRIEND) {
                holder.setVisible(R.id.tv_is_friend, false);
                holder.setVisible(R.id.acb_add, true);
                holder.setVisible(R.id.acb_invite, false);
            }
            // 使用我们的软件，而且是好友-2
            else if (cellphoneContact.getStatus() == CellphoneContact.TYPE_IS_FRIEND) {
                holder.setVisible(R.id.tv_is_friend, true);
                holder.setVisible(R.id.acb_add, false);
                holder.setVisible(R.id.acb_invite, false);
            }
            holder.setText(R.id.tv_name, cellphoneContact.getTarget());
            holder.setText(R.id.tv_cellphone, cellphoneContact.getPhone());
            // 没有使用燕尾帽软件的情况
            holder.setOnClickListener(R.id.acb_invite, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(CellphoneContactActivity.this)
                            .setTitle(R.string.tips)
                            .setMessage("是否发送邀请短信")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.SEND_SMS}, new PermissionListener() {
                                        @Override
                                        public void onGranted() {
                                            sendSMS(cellphoneContact.getPhone());
                                        }

                                        @Override
                                        public void onDenied(List<String> deniedPermission) {

                                        }
                                    });
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            });
            // 使用燕尾帽但不是好友的情况
            holder.setOnClickListener(R.id.acb_add, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 跳转到好友详情页面
                    Intent intent = ContactDetailActivity.getIntent(CellphoneContactActivity.this,
                            cellphoneContact.getRegisterId());
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 发送手机短信
     *
     * @param cellphone
     */
    private void sendSMS(String cellphone) {
        Uri smsToUri = Uri.parse("smsto:" + cellphone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", "我在使用燕尾帽app，非常好用哦，推荐你也来下载使用吧。下载地址：http://www.buzzlysoft.com");
        startActivity(intent);
    }
}
