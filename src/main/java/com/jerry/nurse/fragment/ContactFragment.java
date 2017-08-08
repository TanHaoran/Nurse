package com.jerry.nurse.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jerry.nurse.R;
import com.jerry.nurse.activity.AddContactActivity;
import com.jerry.nurse.activity.ContactDetailActivity;
import com.jerry.nurse.activity.ContactListActivity;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactHeaderBean;
import com.jerry.nurse.model.ContactTopHeaderBean;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.HeaderRecyclerAndFooterWrapperAdapter;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.ViewHolder;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by Jerry on 2017/7/15.
 */

public class ContactFragment extends BaseFragment {

    @Bind(R.id.rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_side_bar_hint)
    TextView mHintTextView;

    @Bind(R.id.ib_index)
    IndexBar mIndexBar;

    private LinearLayoutManager mManager;

    private ProgressDialogManager mProgressDialogManager;

    //设置给InexBar、ItemDecoration的完整数据集
    private List<BaseIndexPinyinBean> mSourceDatas;
    //头部数据源
    private List<ContactHeaderBean> mHeaderDatas;
    //主体部分数据源（城联系人据）
    private List<Contact> mBodyDatas;

    private ContactAdapter mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;

    private SuspensionDecoration mDecoration;

    private LoginInfo mLoginInfo;

    public static ContactFragment newInstance() {

        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_contact;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(getActivity());
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        updateView(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getHospitalInfo(mLoginInfo.getRegisterId());
    }

    /**
     * 获取用户医院信息
     */
    private void getHospitalInfo(final String registerId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_USER_HOSPITAL_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        updateView(true);
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        try {
                            mLoginInfo = new Gson().fromJson(response, LoginInfo.class);
                            // 更新界面
                            updateView(true);
                        } catch (JsonSyntaxException e) {
                            L.i("获取医院信息失败");
                            e.printStackTrace();
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

        mRecyclerView.setLayoutManager(mManager = new LinearLayoutManager(getActivity()));

        mSourceDatas = new ArrayList<>();
        mHeaderDatas = new ArrayList<>();
        List<Contact> collections = new ArrayList<>();
        Contact contact = new Contact();
        contact.setNickName("联系人1");
        Contact contact2 = new Contact();
        contact2.setNickName("联系人2");
        collections.add(contact);
        collections.add(contact2);

        mHeaderDatas.add(new ContactHeaderBean(collections, "    收藏联系人", "收"));
        mSourceDatas.addAll(mHeaderDatas);

        mAdapter = new ContactAdapter(getActivity(), R.layout.item_contact, mBodyDatas);

        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(mAdapter) {

            @Override
            protected void onBindHeaderHolder(com.jerry.nurse.util.ViewHolder holder, int headerPos, int layoutId, Object o) {
                switch (layoutId) {
                    case R.layout.item_contact_header:
                        final ContactHeaderBean contactHeaderBean = (ContactHeaderBean) o;

                        RecyclerView recyclerView = holder.getView(R.id.rv_collection);
                        recyclerView.setAdapter(
                                new CommonAdapter<Contact>(getActivity(), R.layout.meituan_item_header_item, contactHeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final Contact contact) {
                                        holder.setText(R.id.tv_nickname, contact.getNickName());
                                        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = ContactDetailActivity.getIntent(getActivity(), "");
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                });
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        break;
                    case R.layout.item_contact_header_top:
                        final ContactTopHeaderBean contactTopHeaderBean = (ContactTopHeaderBean) o;
                        holder.setText(R.id.tv_nickname, contactTopHeaderBean.getTxt());
                        holder.getView(R.id.ll_group).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (contactTopHeaderBean.getTxt().equals("当前科室")) {
                                    Intent intent = ContactListActivity.getIntent(getActivity());
                                    startActivity(intent);
                                }
                                Toast.makeText(getActivity(), contactTopHeaderBean.getTxt(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        };

        // 如果都通过审核了就可以显示医院和科室的信息
        if (OfficeFragment.checkPermission()) {

            mHeaderAdapter.setHeaderView(0, R.layout.item_contact_header_top,
                    new ContactTopHeaderBean(mLoginInfo.getDepartmentName()));
            mHeaderAdapter.setHeaderView(1, R.layout.item_contact_header_top,
                    new ContactTopHeaderBean(mLoginInfo.getHospitalName()));
        }

        mHeaderAdapter.setHeaderView(2, R.layout.item_contact_header, mHeaderDatas.get(0));

        mRecyclerView.setAdapter(mHeaderAdapter);

        if (!isUpdate) {
            mRecyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(getActivity(), mSourceDatas)
                    .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaderDatas.size()));
        }
        mIndexBar.setmPressedShowTextView(mHintTextView)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager)//设置RecyclerView的LayoutManager
                .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaderDatas.size());

        initDatas(getResources().getStringArray(R.array.provinces));
    }


    /**
     * 组织数据源
     *
     * @param data
     * @return
     */
    private void initDatas(final String[] data) {
        mBodyDatas = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            Contact cityBean = new Contact();
            cityBean.setNickName(data[i]);//设置昵称
            mBodyDatas.add(cityBean);
        }
        //先排序
        mIndexBar.getDataHelper().sortSourceDatas(mBodyDatas);

        mAdapter.setDatas(mBodyDatas);
        mHeaderAdapter.notifyDataSetChanged();
        mSourceDatas.addAll(mBodyDatas);


        mIndexBar.setmSourceDatas(mSourceDatas)//设置数据
                .invalidate();
        mDecoration.setmDatas(mSourceDatas);

        mHeaderAdapter.notifyDataSetChanged();

    }

    class ContactAdapter extends CommonAdapter<Contact> {
        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final Contact cityBean) {
            holder.setText(R.id.tv_nickname, cityBean.getNickName());
            holder.getView(R.id.ll_contact).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ContactDetailActivity.getIntent(getActivity(), "");
                    startActivity(intent);
                }
            });
        }
    }

    @OnClick(R.id.ib_search)
    void onSearch(View view) {

    }

    @OnClick(R.id.ib_add)
    void onAdd(View view) {
        Intent intent = AddContactActivity.getIntent(getActivity());
        startActivity(intent);
    }
}
