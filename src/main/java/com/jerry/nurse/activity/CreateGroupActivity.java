package com.jerry.nurse.activity;

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
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactHeaderBean;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.ContactTopHeaderBean;
import com.jerry.nurse.model.CreateGroupResult;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.HeaderRecyclerAndFooterWrapperAdapter;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.MessageManager;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.ViewHolder;
import com.jerry.nurse.view.SelectView;
import com.jerry.nurse.view.TitleBar;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class CreateGroupActivity extends BaseActivity {

    private static final String EXTRA_GROUP_ID = "extra_group_id";
    private static final String EXTRA_CONTACTS = "extra_contacts";

    @Bind(R.id.tb_group)
    TitleBar mTitleBar;

    @Bind(R.id.rv)
    RecyclerView mRecyclerView;

    @Bind(R.id.tv_hint)
    TextView mHintTextView;

    @Bind(R.id.ib_index)
    IndexBar mIndexBar;

    private LinearLayoutManager mManager;

    //设置给IndexBar、ItemDecoration的完整数据集
    private List<BaseIndexPinyinBean> mSourceDatas;
    //头部数据源
    private List<ContactHeaderBean> mHeaderDatas;
    //主体部分数据源（城联系人据）
    private List<Contact> mBodyDatas;

    private ContactAdapter mAdapter;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;

    private SuspensionDecoration mDecoration;

    private LoginInfo mLoginInfo;

    private String mGroupId;

    private List<Contact> mGroupContacts;

    private Map<String, Contact> mTagLast;

    public static Intent getIntent(Context context, String groupId, List<Contact> contacts) {
        Intent intent = new Intent(context, CreateGroupActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_CONTACTS, (Serializable) contacts);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_create_group;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);

        mLoginInfo = DataSupport.findFirst(LoginInfo.class);

        mGroupContacts = (List<Contact>) getIntent().getSerializableExtra(EXTRA_CONTACTS);
        mGroupId = getIntent().getStringExtra(EXTRA_GROUP_ID);

        if (mGroupContacts != null) {
            for (Contact c : mGroupContacts) {
                if (c.getFriendId() == null) {
                    mGroupContacts.remove(c);
                }
            }
        }

        mBodyDatas = new ArrayList<>();

        updateView(false);

        List<ContactInfo> infos = DataSupport.where("mMyId=? and mIsFriend=?",
                mLoginInfo.getRegisterId(), "1").find(ContactInfo.class);
        for (ContactInfo info : infos) {
            Contact c = new Contact();
            c.setAvatar(info.getAvatar());
            c.setName(info.getName());
            c.setNickName(info.getNickName());
            c.setPhone(info.getCellphone());
            c.setRemark(info.getRemark());
            c.setFriendId(info.getRegisterId());
            c.setFriend(true);

            if (mGroupContacts != null) {
                int i;
                for (i = 0; i < mGroupContacts.size(); i++) {
                    if (mGroupContacts.get(i).getFriendId().equals(c.getFriendId())) {
                        break;
                    }
                }
                if (i == mGroupContacts.size()) {
                    mBodyDatas.add(c);
                }
            } else {
                mBodyDatas.add(c);
            }
        }

        updateView(true);

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener()

        {
            @Override
            public void onRightClick(View view) {
                // 创建群
                if (mGroupId == null) {
                    List<Contact> contacts = new ArrayList<>();
                    Contact me = new Contact();
                    me.setMyId(mLoginInfo.getRegisterId());
                    me.setNickName(mLoginInfo.getNickName());
                    me.setName(mLoginInfo.getName());
                    contacts.add(me);
                    for (Contact c : mBodyDatas) {
                        if (c.isChoose()) {
                            Contact friend = new Contact();
                            friend.setFriendId(c.getFriendId());
                            contacts.add(friend);
                        }
                    }
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setRegisterId(mLoginInfo.getRegisterId());
                    groupInfo.setHXNickName(me.getTarget() + "创建的群");
                    groupInfo.setGroupMemberList(contacts);
                    createChatGroup(groupInfo);
                }
                // 添加群成员
                else {
                    List<Contact> cs = new ArrayList<>();
                    for (Contact c : mBodyDatas) {
                        if (c.isChoose()) {
                            Contact contact = new Contact();
                            contact.setName(c.getName());
                            contact.setNickName(c.getNickName());
                            contact.setPhone(c.getPhone());
                            contact.setAvatar(c.getAvatar());
                            contact.setFriendId(c.getFriendId());
                            cs.add(contact);
                        }
                    }
                    addGroupMember(mGroupId, cs);
                }
            }
        });

    }

    /**
     * 创建群组
     *
     * @param groupInfo
     */
    private void createChatGroup(GroupInfo groupInfo) {
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.CREATE_GROUP)
                .content(StringUtil.addModelWithJson(groupInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CreateGroupResult result = new Gson().fromJson(response, CreateGroupResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            GroupInfo groupInfo = result.getBody();
                            groupInfo.setRegisterId(mLoginInfo.getRegisterId());
                            groupInfo.save();
                            MessageManager.saveCreateGroupLocalData(groupInfo);
                            T.showShort(CreateGroupActivity.this, "创建成功");
                            // 创建完群后立刻发送一条消息
                            sendGroupFirstMessage(groupInfo.getHXGroupId(), "我创建了一个群，一起聊天吧！");
                            finish();
                            Intent intent = ChatActivity.getIntent(CreateGroupActivity.this,
                                    result.getBody().getHXGroupId(), true);
                            startActivity(intent);
                        } else {
                            T.showShort(CreateGroupActivity.this, result.getMsg());
                        }
                    }
                });
    }

    /**
     * 发送环信的第一条信息
     *
     * @param hxGroupId
     */
    private void sendGroupFirstMessage(String hxGroupId, String content) {
        EMMessage emMessage = EMMessage.createTxtSendMessage(content, hxGroupId);
        // 给消息添加本人的头像和昵称
        emMessage.setAttribute("avatar", mLoginInfo.getAvatar());
        emMessage.setAttribute("nick", mLoginInfo.getNickName());
        //如果是群聊，设置chattype，默认是单聊
        emMessage.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(emMessage);
        MessageManager.saveChatMessageData(emMessage, true);
    }

    /**
     * 填充数据
     *
     * @param isUpdate 是否是更新界面
     */
    private void updateView(boolean isUpdate) {

        mRecyclerView.setLayoutManager(mManager = new LinearLayoutManager(CreateGroupActivity.this));

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

//        mHeaderDatas.add(new ContactHeaderBean(collections, "    收藏联系人", "☆"));
        mSourceDatas.addAll(mHeaderDatas);

        mAdapter = new ContactAdapter(CreateGroupActivity.this, R.layout.item_contact_choose, mBodyDatas);

        // 头部适配器
        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(mAdapter) {

            @Override
            protected void onBindHeaderHolder(com.jerry.nurse.util.ViewHolder holder, int headerPos, int layoutId, Object o) {
                switch (layoutId) {
                    case R.layout.item_contact_header:
                        final ContactHeaderBean contactHeaderBean = (ContactHeaderBean) o;

                        RecyclerView recyclerView = holder.getView(R.id.rv_collection);
                        recyclerView.setAdapter(
                                new CommonAdapter<Contact>(CreateGroupActivity.this, R.layout.meituan_item_header_item_choose, contactHeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final Contact contact) {
                                        final SelectView selectView = holder.getView(R.id.sv_choose);
                                        selectView.setSelected(contact.isChoose());
                                        holder.setText(R.id.tv_nickname, contact.getNickName());
                                        holder.getView(R.id.rl_contact).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                contact.setChoose(!contact.isChoose());
                                                selectView.toggle();
                                                notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });
                        recyclerView.setLayoutManager(new LinearLayoutManager(CreateGroupActivity.this));
                        break;
                    case R.layout.item_contact_header_top:
                        final ContactTopHeaderBean contactTopHeaderBean = (ContactTopHeaderBean) o;
                        holder.setText(R.id.tv_nickname, contactTopHeaderBean.getTxt());
                        holder.getView(R.id.ll_group).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (contactTopHeaderBean.getTxt().equals("当前科室")) {
                                    Intent intent = ContactListActivity.getIntent(CreateGroupActivity.this,
                                            null);
                                    startActivity(intent);
                                }
                                Toast.makeText(CreateGroupActivity.this, contactTopHeaderBean.getTxt(),
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
//        if (OfficeFragment.checkPermission()) {
//            mHeaderAdapter.setHeaderView(0, R.layout.item_contact_header_top,
//                    new ContactTopHeaderBean(mLoginInfo.getDepartmentName()));
//            mHeaderAdapter.setHeaderView(1, R.layout.item_contact_header_top,
//                    new ContactTopHeaderBean(mLoginInfo.getHospitalName()));
//        }

//        mHeaderAdapter.setHeaderView(2, R.layout.item_contact_header, mHeaderDatas.get(0));

        mRecyclerView.setAdapter(mHeaderAdapter);

        if (!isUpdate) {
            mRecyclerView.addItemDecoration(mDecoration = new SuspensionDecoration(CreateGroupActivity.this, mSourceDatas)
                    .setHeaderViewCount(mHeaderAdapter.getHeaderViewCount() - mHeaderDatas.size()));
        }
        mIndexBar.setPressedShowTextView(mHintTextView)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setLayoutManager(mManager)//设置RecyclerView的LayoutManager
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


        mIndexBar.setSourceDatas(mSourceDatas)//设置数据
                .invalidate();
        mDecoration.setDatas(mSourceDatas);

        mHeaderAdapter.notifyDataSetChanged();
    }

    /**
     * 添加群成员
     */
    private void addGroupMember(final String groupId, final List<Contact> contacts) {
        if (contacts.size() == 0) {
            finish();
            return;
        }
        GroupInfo info = new GroupInfo();
        info.setHXGroupId(groupId);
        info.setGroupMemberList(contacts);
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.ADD_GROUP_MEMBER)
                .content(StringUtil.addModelWithJson(info))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            T.showShort(CreateGroupActivity.this, "操作成功");
                            setResult(RESULT_OK);
                            StringBuilder sb = new StringBuilder();
                            for (Contact c : contacts) {
                                sb.append(c.getTarget() + ",");
                            }
                            sendGroupFirstMessage(groupId, "我邀请了" + sb.substring(0, sb.length() - 1) + "加入本群");
                            finish();
                        } else {
                            T.showShort(CreateGroupActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }

    class ContactAdapter extends CommonAdapter<Contact> {
        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final Contact contact) {
            String tag = contact.getBaseIndexTag();
            Contact lastContact = (Contact) mIndexBar.getDataHelper().getLast(tag);
            if (lastContact.getFriendId().equals(contact.getFriendId())) {
                holder.setVisible(R.id.v_divider, false);
            } else {
                holder.setVisible(R.id.v_divider, true);
            }

            ImageView imageView = holder.getView(R.id.iv_avatar);
            Glide.with(CreateGroupActivity.this).load(contact.getAvatar())
                    .placeholder(R.drawable.icon_avatar_default).into(imageView);
            final SelectView selectView = holder.getView(R.id.sv_choose);
            selectView.setSelected(contact.isChoose());
            holder.setText(R.id.tv_nickname, contact.getTarget());

            holder.setOnClickListener(R.id.sv_choose, null);

            holder.getView(R.id.rl_contact).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (contact.isChoose()) {
                        contact.setChoose(false);
                        selectView.setSelected(false);
                    } else {
                        contact.setChoose(true);
                        selectView.setSelected(true);
                    }
                    setDatas(mBodyDatas);
                    notifyDataSetChanged();

                    if (mGroupId == null) {
                        List<Contact> contacts = new ArrayList<>();
                        Contact me = new Contact();
                        me.setMyId(mLoginInfo.getRegisterId());
                        me.setNickName(mLoginInfo.getNickName());
                        me.setName(mLoginInfo.getName());
                        contacts.add(me);
                        for (Contact c : mBodyDatas) {
                            if (c.isChoose()) {
                                Contact friend = new Contact();
                                friend.setFriendId(c.getFriendId());
                                contacts.add(friend);
                            }
                        }
                        GroupInfo groupInfo = new GroupInfo();
                        groupInfo.setRegisterId(mLoginInfo.getRegisterId());
                        groupInfo.setHXNickName(me.getTarget() + "创建的群");
                        groupInfo.setGroupMemberList(contacts);
                        L.i("选择了人数：" + groupInfo.getGroupMemberList().size());
                    }
                }
            });
        }
    }
}
