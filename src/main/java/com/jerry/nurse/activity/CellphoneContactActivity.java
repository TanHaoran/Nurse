package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.CellphoneContact;
import com.jerry.nurse.util.CellphoneContactUtil;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.OnItemClickListener;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.ViewHolder;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

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

    private LinearLayoutManager mManager;

    private ProgressDialogManager mProgressDialogManager;

    //设置给InexBar、ItemDecoration的完整数据集
    private List<BaseIndexPinyinBean> mSourceDatas;

    //主体部分数据源（城联系人据）
    private List<CellphoneContact> mBodyDatas;

    private CellphoneContactAdapter mAdapter;

    private SuspensionDecoration mDecoration;

    private LoginInfo mLoginInfo;

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
        mProgressDialogManager = new ProgressDialogManager(this);
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);

        // 读取手机通讯录联系人
        loadCellphoneContact();
        //      updateView(false);
    }

    /**
     * 读取手机通讯录联系人
     */
    private void loadCellphoneContact() {
        mBodyDatas = CellphoneContactUtil.getPhoneNumberFromMobile(this);
        postCellphoneContact();
    }

    /**
     *
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
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {

                        } else {
                            L.i(commonResult.getMsg());
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

        mRecyclerView.setLayoutManager(mManager = new LinearLayoutManager(this));

        mSourceDatas = new ArrayList<>();

        mAdapter = new CellphoneContactAdapter(this, R.layout.item_cellphone_contact, mBodyDatas);

        mRecyclerView.setAdapter(mAdapter);

        if (!isUpdate) {
            mRecyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(this, mSourceDatas));
        }
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
        ;

        initDatas(getResources().getStringArray(R.array.provinces));
    }


    /**
     * 组织数据源
     *
     * @param data
     * @return
     */
    private void initDatas(final String[] data) {
        //先排序
        mIndexBar.getDataHelper().sortSourceDatas(mBodyDatas);

        mAdapter.setDatas(mBodyDatas);
        mSourceDatas.addAll(mBodyDatas);


        mIndexBar.setmSourceDatas(mSourceDatas)//设置数据
                .invalidate();
        mDecoration.setmDatas(mSourceDatas);

        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                Intent intent = ContactDetailActivity.getIntent(CellphoneContactActivity.this, "");
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
    }

    class CellphoneContactAdapter extends CommonAdapter<CellphoneContact> {
        public CellphoneContactAdapter(Context context, int layoutId, List<CellphoneContact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final CellphoneContact cellphoneContact) {
            holder.setText(R.id.tv_nickname, cellphoneContact.getName());
            holder.setText(R.id.tv_cellphone, cellphoneContact.getPhone());
            holder.getView(R.id.ll_cellphone_contact).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

}
