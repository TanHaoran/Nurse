package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CellphoneContactResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.CellphoneContact;
import com.jerry.nurse.util.CellphoneContactUtil;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.ViewHolder;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class CellphoneContactActivity extends BaseActivity {

    @Bind(R.id.rv_contact)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_side_bar_hint)
    TextView mTvSideBarHint;

    @Bind(R.id.ib_index)
    IndexBar mIndexBar;

    //设置给InexBar、ItemDecoration的完整数据集
    private List<BaseIndexPinyinBean> mSourceDatas;

    //主体部分数据源（城联系人据）
    private List<CellphoneContact> mBodyDatas = new ArrayList<>();

    private CellphoneContactAdapter mAdapter;

    private SuspensionDecoration mDecoration;

    private Map<String, CellphoneContact> mTagLast;

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
        mBodyDatas.add(me);
        // 读取手机通讯录联系人
        List<CellphoneContact> cellphoneContacts = CellphoneContactUtil.getPhoneNumberFromMobile(this);
        mBodyDatas.addAll(cellphoneContacts);
        postCellphoneContact();
    }


    /**
     * 获取手机联系人中注册情况
     */
    private void postCellphoneContact() {
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.GET_CELLPHONE_CONTACT)
                .content(StringUtil.addModelWithJson(mBodyDatas))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CellphoneContactResult result = new Gson().fromJson(response, CellphoneContactResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mBodyDatas = result.getBody();
                            updateView(false);
                        } else {
                            L.i(result.getMsg());
                        }
                    }
                });
    }

    /**
     * 填充数据
     *
     * @param isUpdate 是否是更新界面
     */
    private void updateView(boolean isUpdate) {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        mSourceDatas = new ArrayList<>();

        mAdapter = new CellphoneContactAdapter(this, R.layout.item_cellphone_contact, mBodyDatas);

        mRecyclerView.setAdapter(mAdapter);

        if (!isUpdate) {
            mRecyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(this, mSourceDatas));
        }
        mIndexBar.setmPressedShowTextView(mTvSideBarHint) //设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(manager);//设置RecyclerView的LayoutManager

        initDatas();
    }


    /**
     * 组织数据源
     *
     * @return
     */
    private void initDatas() {
        //先排序
        mIndexBar.getDataHelper().sortSourceDatas(mBodyDatas);

        // 存储每一个Tag下的最后一个元素
        mTagLast = new HashMap<>();
        for (CellphoneContact c : mBodyDatas) {
            mTagLast.put(c.getBaseIndexTag(), c);
        }

        mAdapter.setDatas(mBodyDatas);
        mSourceDatas.addAll(mBodyDatas);


        mIndexBar.setmSourceDatas(mSourceDatas)//设置数据
                .invalidate();
        mDecoration.setmDatas(mSourceDatas);

        mAdapter.notifyDataSetChanged();
    }

    class CellphoneContactAdapter extends CommonAdapter<CellphoneContact> {
        public CellphoneContactAdapter(Context context, int layoutId, List<CellphoneContact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final CellphoneContact cellphoneContact) {
            String tag = cellphoneContact.getBaseIndexTag();
            CellphoneContact lastContact = mTagLast.get(tag);
            if (lastContact.getPhone().equals(cellphoneContact
                    .getPhone()) && holder.getLayoutPosition()
                    != mBodyDatas.size() - 1
                    ) {
                holder.setVisible(R.id.v_divider, false);
            } else {
                holder.setVisible(R.id.v_divider, true);
            }

            // 没有使用我们的软件
            if (cellphoneContact.getStatus() == CellphoneContact.TYPE_NOT_USAGE) {
                holder.setVisible(R.id.tv_is_friend, false);
                holder.setVisible(R.id.acb_add, false);
                holder.setVisible(R.id.acb_invite, true);
            }
            // 使用我们的软件，但不是好友
            else if (cellphoneContact.getStatus() == CellphoneContact.TYPE_NOT_FRIEND) {
                holder.setVisible(R.id.tv_is_friend, false);
                holder.setVisible(R.id.acb_add, true);
                holder.setVisible(R.id.acb_invite, false);
            }
            // 使用我们的软件，而且是好友
            else if (cellphoneContact.getStatus() == CellphoneContact.TYPE_IS_FRIEND) {
                holder.setVisible(R.id.tv_is_friend, true);
                holder.setVisible(R.id.acb_add, false);
                holder.setVisible(R.id.acb_invite, false);
            }
            holder.setText(R.id.tv_nickname, cellphoneContact.getName());
            holder.setText(R.id.tv_cellphone, cellphoneContact.getPhone());
            holder.getView(R.id.ll_cellphone_contact).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 没有使用我们的软件
                    if (cellphoneContact.getStatus() == CellphoneContact.TYPE_NOT_USAGE) {

                    }
                    // 使用我们的软件，就直接跳转页面
                    else if (cellphoneContact.getStatus() == CellphoneContact.TYPE_NOT_FRIEND ||
                            cellphoneContact.getStatus() == CellphoneContact.TYPE_IS_FRIEND) {
                        Intent intent = ContactDetailActivity.getIntent(CellphoneContactActivity.this,
                                cellphoneContact.getRegisterId());
                        startActivity(intent);
                    }
                }
            });
        }
    }

}
