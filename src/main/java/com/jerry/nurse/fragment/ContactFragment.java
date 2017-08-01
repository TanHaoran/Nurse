package com.jerry.nurse.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jerry.nurse.R;
import com.jerry.nurse.adapter.ContactAdapter;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactHeaderBean;
import com.jerry.nurse.model.MeituanTopHeaderBean;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.HeaderRecyclerAndFooterWrapperAdapter;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ViewHolder;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Jerry on 2017/7/15.
 */

public class ContactFragment extends Fragment {

    @Bind(R.id.rv)
    RecyclerView mRv;

    @Bind(R.id.tvSideBarHint)
    TextView mTvSideBarHint;

    @Bind(R.id.indexBar)
    IndexBar mIndexBar;

    private LinearLayoutManager mManager;

    //设置给InexBar、ItemDecoration的完整数据集
    private List<BaseIndexPinyinBean> mSourceDatas;
    //头部数据源
    private List<ContactHeaderBean> mHeaderDatas;
    //主体部分数据源（城市数据）
    private List<Contact> mBodyDatas;

    private ContactAdapter mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;

    private SuspensionDecoration mDecoration;

    public static ContactFragment newInstance() {

        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        L.i("初始化联系人页面");
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

        mRv.setLayoutManager(mManager = new LinearLayoutManager(getActivity()));

        mSourceDatas = new ArrayList<>();
        mHeaderDatas = new ArrayList<>();
        List<Contact> collections = new ArrayList<>();
        Contact contact = new Contact();
        contact.setNickName("联系人1");
        Contact contact2 = new Contact();
        contact2.setNickName("联系人2");
        collections.add(contact);
        collections.add(contact2);
        mHeaderDatas.add(new ContactHeaderBean(collections, "收藏联系人", "收"));
        mSourceDatas.addAll(mHeaderDatas);

        mAdapter = new ContactAdapter(getActivity(), R.layout.meituan_item_select_city, mBodyDatas);

        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(mAdapter) {

            @Override
            protected void onBindHeaderHolder(com.jerry.nurse.util.ViewHolder holder, int headerPos, int layoutId, Object o) {
                switch (layoutId) {
                    case R.layout.meituan_item_header:
                        final ContactHeaderBean contactHeaderBean = (ContactHeaderBean) o;
                        //网格
                        RecyclerView recyclerView = holder.getView(R.id.rvCity);
                        recyclerView.setAdapter(
                                new CommonAdapter<Contact>(getActivity(), R.layout.meituan_item_header_item, contactHeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final Contact contact) {
                                        holder.setText(R.id.tvName, contact.getNickName());
                                        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(mContext, "昵称:" + contact.getNickName(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        break;
                    case R.layout.meituan_item_header_top:
                        final MeituanTopHeaderBean meituanTopHeaderBean = (MeituanTopHeaderBean) o;
                        holder.setText(R.id.tvCurrent, meituanTopHeaderBean.getTxt());
                        holder.getView(R.id.ll_group).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), meituanTopHeaderBean.getTxt(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        };

        mHeaderAdapter.setHeaderView(0, R.layout.meituan_item_header_top, new MeituanTopHeaderBean("全院"));
        mHeaderAdapter.setHeaderView(1, R.layout.meituan_item_header_top, new MeituanTopHeaderBean("当前科室"));
        mHeaderAdapter.setHeaderView(2, R.layout.meituan_item_header, mHeaderDatas.get(0));


        mRv.setAdapter(mHeaderAdapter);
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(getActivity(), mSourceDatas)
                .setmTitleHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics()))
                .setColorTitleBg(0xffefefef)
                .setTitleFontSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()))
                .setColorTitleFont(getActivity().getResources().getColor(android.R.color.black))
                .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaderDatas.size()));
        mRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
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
            cityBean.setNickName(data[i]);//设置城市名称
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
}
