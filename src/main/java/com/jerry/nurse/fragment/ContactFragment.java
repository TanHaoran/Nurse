package com.jerry.nurse.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jerry.nurse.R;
import com.jerry.nurse.activity.AddContactActivity;
import com.jerry.nurse.activity.ContactDetailActivity;
import com.jerry.nurse.activity.ContactListActivity;
import com.jerry.nurse.activity.GroupListActivity;
import com.jerry.nurse.activity.OfficeListActivity;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactHeaderBean;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.ContactTopHeaderBean;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.HeaderRecyclerAndFooterWrapperAdapter;
import com.jerry.nurse.util.ViewHolder;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by Jerry on 2017/7/15.
 */

public class ContactFragment extends BaseFragment {

    @Bind(R.id.ll_search)
    LinearLayout mSearchLayout;

    @Bind(R.id.et_keyword)
    EditText mKeyWordEditText;

    @Bind(R.id.tv_cancel)
    TextView mCancelTextView;

    @Bind(R.id.rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_hint)
    TextView mHintTextView;

    @Bind(R.id.ib_index)
    IndexBar mIndexBar;

    private LinearLayoutManager mManager;

    // 主数据集
    private List<BaseIndexPinyinBean> mDatas;
    // 头部数据集
    private List<ContactHeaderBean> mHeaders;
    // 联系人数据集
    private List<Contact> mContacts;

    private ContactAdapter mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;

    private SuspensionDecoration mDecoration;

    private LoginInfo mLoginInfo;

    private Map<String, Contact> mTagLast;

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
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        mContacts = new ArrayList<>();
        updateView(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        mContacts = new ArrayList<>();
        // 读取好友列表
        loadContacts();
        updateView(true);
    }

    /**
     * 读取好友列表
     */
    private void loadContacts() {
        List<ContactInfo> infos = DataSupport.where("mMyId=? and " +
                "mIsFriend=?", mLoginInfo.getRegisterId(), "1")
                .find(ContactInfo.class);
        // 组装成可以排序的列表
        for (ContactInfo info : infos) {
            Contact c = new Contact();
            c.setAvatar(info.getAvatar());
            c.setName(info.getName());
            c.setNickName(info.getNickName());
            c.setPhone(info.getCellphone());
            c.setRemark(info.getRemark());
            c.setFriendId(info.getRegisterId());
            c.setFriend(info.isFriend());
            mContacts.add(c);
        }
    }

    /**
     * 填充数据
     *
     * @param isUpdate 是否是更新界面
     */
    private void updateView(boolean isUpdate) {

        mManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mManager);

        mDatas = new ArrayList<>();
        mHeaders = new ArrayList<>();
        // 构造收藏联系人
        List<Contact> collections = new ArrayList<>();
//        Contact contact = new Contact();
//        contact.setNickName("联系人1");
//        Contact contact2 = new Contact();
//        contact2.setNickName("联系人2");
//        collections.add(contact);
//        collections.add(contact2);

//        mHeaders.add(new ContactHeaderBean(collections, "    收藏联系人", "☆"));
        mDatas.addAll(mHeaders);

        mAdapter = new ContactAdapter(getActivity(), R.layout.item_contact, mContacts);

        // 头部适配器
        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(mAdapter) {

            @Override
            protected void onBindHeaderHolder(com.jerry.nurse.util.ViewHolder holder, int headerPos, int layoutId, Object o) {
                switch (layoutId) {
                    // 收藏联系人
                    case R.layout.item_contact_header:
                        final ContactHeaderBean contactHeaderBean = (ContactHeaderBean) o;

                        RecyclerView recyclerView = holder.getView(R.id.rv_collection);
                        recyclerView.setAdapter(
                                new CommonAdapter<Contact>(getActivity(), R.layout.item_header_item, contactHeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final Contact contact) {
//                                        holder.setText(R.id.tv_nickname, contact.getNickName());
//                                        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = ContactDetailActivity.getIntent(getActivity(), "");
//                                                startActivity(intent);
//                                            }
//                                        });
                                    }
                                });
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        break;
                    // 科室、医院、我的群
                    case R.layout.item_contact_header_top:
                        final ContactTopHeaderBean contactTopHeaderBean = (ContactTopHeaderBean) o;
                        holder.setText(R.id.tv_nickname, contactTopHeaderBean.getTxt());
                        if ("我的群".equals(contactTopHeaderBean.getTxt())) {
                            holder.setImageResource(R.id.iv_avatar, R.drawable.icon_qlt);
                            if (mContacts.size() == 0) {
                                holder.setVisible(R.id.v_divider, true);
                            } else {
                                holder.setVisible(R.id.v_divider, false);
                            }
                        } else if (contactTopHeaderBean.getTxt().equals(mLoginInfo.getDepartmentName())) {
                            holder.setImageResource(R.id.iv_avatar, R.drawable.icon_ks);
                            holder.setText(R.id.tv_count, " (" + mLoginInfo.getDepartmentUserCount() + "人)");
                        } else if (contactTopHeaderBean.getTxt().equals(mLoginInfo.getHospitalName())) {
                            holder.setImageResource(R.id.iv_avatar, R.drawable.icon_yy);
                        }


                        holder.getView(R.id.ll_group).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 点击科室
                                if (contactTopHeaderBean.getTxt().equals(mLoginInfo.getDepartmentName())) {
                                    Intent intent = ContactListActivity.getIntent(getActivity(), null);
                                    startActivity(intent);
                                }
                                // 点击医院
                                else if (contactTopHeaderBean.getTxt().equals(mLoginInfo.getHospitalName())) {
                                    Intent intent = OfficeListActivity.getIntent(getActivity(),
                                            mLoginInfo.getHospitalId(), 1);
                                    startActivity(intent);
                                }
                                // 点击我的群
                                else if (contactTopHeaderBean.getTxt().equals("我的群")) {
                                    Intent intent = GroupListActivity.getIntent(getActivity());
                                    startActivity(intent);
                                }
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

        mHeaderAdapter.setHeaderView(2, R.layout.item_contact_header_top,
                new ContactTopHeaderBean("我的群"));

//        mHeaderAdapter.setHeaderView(3, R.layout.item_contact_header, mHeaders.get(0));

        mRecyclerView.setAdapter(mHeaderAdapter);

        if (!isUpdate) {
            mRecyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(getActivity(), mDatas)
                    .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaders.size()));
        }
        mIndexBar.setPressedShowTextView(mHintTextView)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setLayoutManager(mManager)//设置RecyclerView的LayoutManager
                .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaders.size());

        initDatas();
    }


    /**
     * 组织数据源
     */
    private void initDatas() {

        //先排序
        mIndexBar.getDataHelper().sortSourceDatas(mContacts);

        mAdapter.setDatas(mContacts);
        mHeaderAdapter.notifyDataSetChanged();
        mDatas.addAll(mContacts);

        mIndexBar.setSourceDatas(mDatas)//设置数据
                .invalidate();
        mDecoration.setDatas(mDatas);

        mHeaderAdapter.notifyDataSetChanged();
    }

    class ContactAdapter extends CommonAdapter<Contact> {
        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final Contact contact) {
            String tag = contact.getBaseIndexTag();
            Contact lastContact = (Contact) mIndexBar.getDataHelper().getLast(tag);
            if (lastContact.getFriendId().equals(contact.getFriendId()) &&
                    holder.getLayoutPosition() != mContacts.size()) {
                holder.setVisible(R.id.v_divider, false);
            } else {
                holder.setVisible(R.id.v_divider, true);
            }

            ImageView imageView = holder.getView(R.id.iv_avatar);
            Glide.with(getActivity()).load(contact.getAvatar())
                    .placeholder(R.drawable.icon_avatar_default).into(imageView);
            holder.setText(R.id.tv_nickname, contact.getTarget());
            holder.getView(R.id.rl_contact).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ContactDetailActivity.getIntent(getActivity(), contact.getFriendId());
                    startActivity(intent);
                }
            });
        }
    }

    @OnClick(R.id.ib_search)
    void onSearch(View view) {
        mSearchLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_cancel)
    void onCancel(View view) {
        mKeyWordEditText.setText("");
        mSearchLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.ib_add)
    void onAdd(View view) {
        Intent intent = AddContactActivity.getIntent(getActivity());
        startActivity(intent);
    }
}
