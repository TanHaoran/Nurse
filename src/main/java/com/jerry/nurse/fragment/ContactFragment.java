package com.jerry.nurse.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.activity.AddContactActivity;
import com.jerry.nurse.activity.ContactDetailActivity;
import com.jerry.nurse.activity.ContactListActivity;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactHeaderBean;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.ContactTopHeaderBean;
import com.jerry.nurse.model.FriendListResult;
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

import static com.jerry.nurse.constant.ServiceConstant.AVATAR_ADDRESS;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;


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
        mBodyDatas = new ArrayList<>();
        updateView(false);
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        getFriendList(mLoginInfo.getRegisterId());
    }


    /**
     * 获取用户好友资料
     */
    private void getFriendList(final String registerId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_FRIEND_LIST)
                .addParams("MyId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        updateView(true);
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        FriendListResult friendListResult = new Gson().fromJson(response, FriendListResult.class);
                        if (friendListResult.getCode() == RESPONSE_SUCCESS) {
                            mBodyDatas = friendListResult.getBody();
                            L.i("读取到了" + mBodyDatas.size());
                            if (mBodyDatas == null) {
                                mBodyDatas = new ArrayList();
                            } else {
                                updateContactInfoData(mBodyDatas);
                            }
                            updateView(true);
                        }
                    }
                });
    }

    /**
     * 更新本地联系人数据
     *
     * @param bodyDatas
     */
    private void updateContactInfoData(List<Contact> bodyDatas) {
        try {
            DataSupport.deleteAll(ContactInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Contact contact : bodyDatas) {
            ContactInfo info = new ContactInfo();
            info.setAvatar(contact.getAvatar());
            info.setName(contact.getName());
            info.setNickName(contact.getNickName());
            info.setCellphone(contact.getPhone());
            info.setRemark(contact.getRemark());
            info.setRegisterId(contact.getFriendId());
            info.save();
        }
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
        // 构造收藏联系人
        List<Contact> collections = new ArrayList<>();
//        Contact contact = new Contact();
//        contact.setNickName("联系人1");
//        Contact contact2 = new Contact();
//        contact2.setNickName("联系人2");
//        collections.add(contact);
//        collections.add(contact2);

        mHeaderDatas.add(new ContactHeaderBean(collections, "    收藏联系人", "☆"));
        mSourceDatas.addAll(mHeaderDatas);

        mAdapter = new ContactAdapter(getActivity(), R.layout.item_contact, mBodyDatas);

        // 头部适配器
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

        initDatas();
    }


    /**
     * 组织数据源
     */
    private void initDatas() {

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
        public void convert(ViewHolder holder, final Contact contact) {
            ImageView imageView = holder.getView(R.id.iv_avatar);
            if (contact.getAvatar().startsWith("http")) {
                Glide.with(getActivity()).load(contact.getAvatar()).into(imageView);
            } else {
                Glide.with(getActivity()).load(AVATAR_ADDRESS + contact.getAvatar()).into(imageView);
            }
            holder.setText(R.id.tv_nickname, contact.getNickName());
            holder.getView(R.id.ll_contact).setOnClickListener(new View.OnClickListener() {
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

    }

    @OnClick(R.id.ib_add)
    void onAdd(View view) {
        Intent intent = AddContactActivity.getIntent(getActivity());
        startActivity(intent);
    }
}
