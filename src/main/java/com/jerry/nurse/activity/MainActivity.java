package com.jerry.nurse.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.fragment.ContactFragment;
import com.jerry.nurse.fragment.MeFragment;
import com.jerry.nurse.fragment.MessageFragment;
import com.jerry.nurse.fragment.OfficeFragment;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.FriendListResult;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.GroupListResult;
import com.jerry.nurse.model.Message;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.EaseMobManager;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class MainActivity extends BaseActivity {

    public static final String ACTION_CHAT_MESSAGE_RECEIVE = "action_chat_message_receive";
    public static final String ACTION_FRIEND_APPLY_RECEIVE = "action_friend_apply_receive";
    public static final String ACTION_J_PUSH_RECEIVE = "action_j_push_receive";

    public static final String EXTRA_CHAT_MESSAGE = "extra_chat_message";
    public static final String EXTRA_J_PUSH_MESSAGE = "extra_j_push_message";

    public static final String EXTRA_FRIEND_APPLY_CONTACT = "extra_friend_apply";

//    @Bind(R.id.bnb_main)
//    BottomNavigationBar mNavigationBar;

    @Bind(R.id.tv_message)
    TextView mMessageTextView;

    @Bind(R.id.tv_office)
    TextView mOfficeTextView;

    @Bind(R.id.tv_contact)
    TextView mContactTextView;

    @Bind(R.id.tv_me)
    TextView mMeTextView;

    private List<Fragment> mFragments;
    private MessageFragment mMessageFragment;
    private OfficeFragment mOfficeFragment;
    private ContactFragment mContactFragment;
    private MeFragment mMeFragment;
    private ProgressDialogManager mProgressDialogManager;

    private EaseMobManager mEaseMobManager;
    private MessageReceiver mMessageReceiver;

    private String mRegisterId;

    private int mCurrentIndex = 0;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mRegisterId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        // 环信登陆
        mEaseMobManager = new EaseMobManager(this);
        mEaseMobManager.login(mRegisterId);

        mProgressDialogManager = new ProgressDialogManager(this);

        // 初始化Fragment
        getFragments();

        // 设置起始页
        setDefaultFragment();

        // 开启广播监听
        mMessageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CHAT_MESSAGE_RECEIVE);
        intentFilter.addAction(ACTION_FRIEND_APPLY_RECEIVE);
        registerReceiver(mMessageReceiver, intentFilter);

        getFriendList(mRegisterId);
        getGroupList(mRegisterId);

        boolean isFirstIn = (boolean) SPUtil.get(this, SPUtil.IS_FIRST_IN, true);
        if (isFirstIn) {
            createWelcomeMessage(mRegisterId);
        }
        SPUtil.put(this, SPUtil.IS_FIRST_IN, false);
    }


    /**
     * 初始化Fragment
     */
    private void getFragments() {
        mFragments = new ArrayList<>();
        mMessageFragment = MessageFragment.newInstance();
        mOfficeFragment = OfficeFragment.newInstance();
        mContactFragment = ContactFragment.newInstance();
        mMeFragment = MeFragment.newInstance();

        mFragments.add(mMessageFragment);
        mFragments.add(mOfficeFragment);
        mFragments.add(mContactFragment);
        mFragments.add(mMeFragment);
    }

    /**
     * 设置起始页
     */
    private void setDefaultFragment() {
        mMessageTextView.setSelected(true);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fl_main, mMessageFragment);
        transaction.commit();
    }

    /**
     * 重置按钮状态
     */
    private void resetTextView() {
        mMessageTextView.setSelected(false);
        mOfficeTextView.setSelected(false);
        mContactTextView.setSelected(false);
        mMeTextView.setSelected(false);
    }

    @OnClick({R.id.rl_message, R.id.rl_office, R.id.rl_contact, R.id.rl_me})
    void onTabSelected(View view) {
        resetTextView();
        switch (view.getId()) {
            case R.id.rl_message:
                mMessageTextView.setSelected(true);
                onTabSelected(0);
                if (mCurrentIndex != 0) {
                    onTabUnselected(mCurrentIndex);
                }
                mCurrentIndex = 0;
                break;
            case R.id.rl_office:
                mOfficeTextView.setSelected(true);
                onTabSelected(1);
                if (mCurrentIndex != 1) {
                    onTabUnselected(mCurrentIndex);
                }
                mCurrentIndex = 1;
                break;
            case R.id.rl_contact:
                mContactTextView.setSelected(true);
                onTabSelected(2);
                if (mCurrentIndex != 2) {
                    onTabUnselected(mCurrentIndex);
                }
                mCurrentIndex = 2;
                break;
            case R.id.rl_me:
                mMeTextView.setSelected(true);
                onTabSelected(3);
                if (mCurrentIndex != 3) {
                    onTabUnselected(mCurrentIndex);
                }
                mCurrentIndex = 3;
                break;
        }
    }

    private void onTabSelected(int position) {
        if (!mFragments.isEmpty()) {
            if (position < mFragments.size()) {
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                Fragment fragment = mFragments.get(position);
                if (fragment.isAdded()) {
                    transaction.replace(R.id.fl_main, fragment);
                } else {
                    transaction.add(R.id.fl_main, fragment);
                }
                transaction.commitAllowingStateLoss();
            }
        }
    }

    public void onTabUnselected(int position) {
        if (!mFragments.isEmpty()) {
            if (position < mFragments.size()) {
                FragmentTransaction transaction = getSupportFragmentManager()
                        .beginTransaction();
                Fragment fragment = mFragments.get(position);
                transaction.remove(fragment);
                transaction.commitAllowingStateLoss();
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEaseMobManager.logout();
        unregisterReceiver(mMessageReceiver);
    }

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 收到消息
            if (ACTION_CHAT_MESSAGE_RECEIVE.equals(intent.getAction())) {
                L.i("接收到消息广播");
                if (mMessageFragment.isVisible()) {
                    mMessageFragment.refresh();
                    return;
                }
                ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra(EXTRA_CHAT_MESSAGE);

                // 发出Notification
                Intent newIntent;
                if (chatMessage.isGroup()) {
                    newIntent = ChatActivity.getIntent(context, chatMessage.getFrom(), true);
                } else {
                    newIntent = ChatActivity.getIntent(context, chatMessage.getFrom(), false);
                }
                PendingIntent pi = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new Notification.Builder(MainActivity.this)
                        .setTicker("新消息")
                        .setSmallIcon(android.R.drawable.ic_menu_info_details)
                        .setContentTitle("新消息提示")
                        .setContentText(chatMessage.getContent())
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(0, notification);
            }
            // 收到好友申请
            else if (ACTION_FRIEND_APPLY_RECEIVE.equals(intent.getAction())) {
                L.i("接收好友申请广播");
                if (mMessageFragment.isVisible()) {
                    mMessageFragment.refresh();
                    return;
                }
                AddFriendApply apply = (AddFriendApply) intent.getSerializableExtra(EXTRA_FRIEND_APPLY_CONTACT);

                // 发出Notification
                Intent newIntent = AddContactApplyActivity.getIntent(context);
                PendingIntent pi = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new Notification.Builder(MainActivity.this)
                        .setTicker("新消息")
                        .setSmallIcon(android.R.drawable.ic_menu_info_details)
                        .setContentTitle("好友申请")
                        .setContentText(apply.getReason())
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(0, notification);
            }
        }
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
                    public void onFilterResponse(String response, int id) {
                        FriendListResult friendListResult = new Gson().fromJson(response, FriendListResult.class);
                        if (friendListResult.getCode() == RESPONSE_SUCCESS) {
                            List<Contact> contacts = friendListResult.getBody();
                            L.i("初始化读取到了" + contacts.size() + "个好友");
                            if (contacts == null) {
                                contacts = new ArrayList();
                            }
                            updateContactInfoData(contacts);

                        }
                    }
                });
    }

    /**
     * 更新本地联系人数据
     *
     * @param bodyDatas
     */
    public static void updateContactInfoData(List<Contact> bodyDatas) {

        List<ContactInfo> infos = null;
        try {
            infos = DataSupport.findAll(ContactInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (infos == null) {
            infos = new ArrayList<>();
        }
        for (Contact contact : bodyDatas) {
            int i;
            for (i = 0; i < infos.size(); i++) {
                // 如果两个信息相同就更新
                ContactInfo info = infos.get(i);
                if (contact.getFriendId().equals(info.getRegisterId())) {
                    info.setAvatar(contact.getAvatar());
                    info.setName(contact.getName());
                    info.setNickName(contact.getNickName());
                    info.setCellphone(contact.getPhone());
                    info.setRemark(contact.getRemark());
                    info.setRegisterId(contact.getFriendId());
                    info.setFriend(contact.isFriend());
                    info.save();
                    L.i("更新一条联系人信息");
                    break;
                }
            }
            // 如果本地数据库没有就创建保存
            if (i == infos.size()) {
                ContactInfo info = new ContactInfo();
                info.setAvatar(contact.getAvatar());
                info.setName(contact.getName());
                info.setNickName(contact.getNickName());
                info.setCellphone(contact.getPhone());
                info.setRemark(contact.getRemark());
                info.setRegisterId(contact.getFriendId());
                info.setFriend(contact.isFriend());
                info.save();
                L.i("新增一条联系人信息");
            }
        }
    }

    /**
     * 更新本地联系人数据
     *
     * @param contact
     */
    public static ContactInfo updateContactInfoData(Contact contact) {

        List<ContactInfo> infos = null;
        try {
            infos = DataSupport.findAll(ContactInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (infos == null) {
            infos = new ArrayList<>();
        }
        int i;
        for (i = 0; i < infos.size(); i++) {
            // 如果两个信息相同就更新
            ContactInfo info = infos.get(i);
            if (contact.getFriendId().equals(info.getRegisterId())) {
                info.setAvatar(contact.getAvatar());
                info.setName(contact.getName());
                info.setNickName(contact.getNickName());
                info.setCellphone(contact.getPhone());
                info.setRemark(contact.getRemark());
                info.setRegisterId(contact.getFriendId());
                info.setFriend(contact.isFriend());
                info.save();
                L.i("更新一条联系人信息");
                return info;
            }
        }
        ContactInfo info = new ContactInfo();
        info.setAvatar(contact.getAvatar());
        info.setName(contact.getName());
        info.setNickName(contact.getNickName());
        info.setCellphone(contact.getPhone());
        info.setRemark(contact.getRemark());
        info.setRegisterId(contact.getFriendId());
        info.save();
        L.i("新增一条联系人信息");
        return info;

    }


    /**
     * 获取群信息
     *
     * @param registerId
     */
    private void getGroupList(String registerId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_GROUP_LIST)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        GroupListResult result = new Gson().fromJson(response, GroupListResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            List<GroupInfo> groupInfo = result.getBody();
                            L.i("初始化读取到了" + groupInfo.size() + "个群");
                            if (groupInfo == null) {
                                groupInfo = new ArrayList();
                            }
                            updateGroupInfoData(groupInfo);
                        }
                    }
                });
    }

    /**
     * 更新本地群信息数据
     *
     * @param bodyDatas
     */
    public static void updateGroupInfoData(List<GroupInfo> bodyDatas) {

        List<GroupInfo> infos = null;
        try {
            infos = DataSupport.findAll(GroupInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (infos == null) {
            infos = new ArrayList<>();
        }
        for (GroupInfo group : bodyDatas) {
            int i;
            for (i = 0; i < infos.size(); i++) {
                // 如果两个信息相同就更新
                GroupInfo info = infos.get(i);
                if (group.getHXGroupId().equals(info.getHXGroupId())) {
                    info.setHXGroupId(group.getHXGroupId());
                    info.setHXNickName(group.getHXNickName());
                    info.setRegisterId(group.getRegisterId());
                    info.setCreateTime(group.getCreateTime());
                    info.setGroupUserCount(group.getGroupUserCount());
                    L.i("更新的群成员数量：" + group.getGroupMemberList().size());
                    // 更新联系人信息
                    updateContactInfoData(group.getGroupMemberList());
                    info.save();
                    break;
                }
            }
            // 如果本地数据库没有就创建保存
            if (i == infos.size()) {
                GroupInfo info = new GroupInfo();
                info.setHXGroupId(group.getHXGroupId());
                info.setHXNickName(group.getHXNickName());
                info.setRegisterId(group.getRegisterId());
                info.setCreateTime(group.getCreateTime());
                info.setGroupUserCount(group.getGroupUserCount());
                L.i("更新的群成员数量：" + group.getGroupMemberList().size());
                // 更新联系人信息
                updateContactInfoData(group.getGroupMemberList());
                info.save();
            }
        }
    }


    /**
     * 更新本地群信息数据
     *
     * @param groupInfo
     */
    public static void updateGroupInfoData(GroupInfo groupInfo) {

        List<GroupInfo> infos = null;
        try {
            infos = DataSupport.findAll(GroupInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (infos == null) {
            infos = new ArrayList<>();
        }
        int i;
        for (i = 0; i < infos.size(); i++) {
            // 如果两个信息相同就更新
            GroupInfo info = infos.get(i);
            if (groupInfo.getHXGroupId().equals(info.getHXGroupId())) {
                info.setHXGroupId(groupInfo.getHXGroupId());
                info.setHXNickName(groupInfo.getHXNickName());
                info.setRegisterId(groupInfo.getRegisterId());
                info.setCreateTime(groupInfo.getCreateTime());
                info.setGroupUserCount(groupInfo.getGroupUserCount());
                L.i("更新的群成员数量：" + groupInfo.getGroupMemberList().size());
                // 更新联系人信息
                updateContactInfoData(groupInfo.getGroupMemberList());
                info.save();
                break;
            }
        }
        // 如果本地数据库没有就创建保存
        if (i == infos.size()) {
            GroupInfo info = new GroupInfo();
            info.setHXGroupId(groupInfo.getHXGroupId());
            info.setHXNickName(groupInfo.getHXNickName());
            info.setRegisterId(groupInfo.getRegisterId());
            info.setCreateTime(groupInfo.getCreateTime());
            info.setGroupUserCount(groupInfo.getGroupUserCount());
            L.i("更新的群成员数量：" + groupInfo.getGroupMemberList().size());
            // 更新联系人信息
            updateContactInfoData(groupInfo.getGroupMemberList());
            info.save();
        }

    }


    /**
     * 创建欢迎消息
     */
    private void createWelcomeMessage(String registerId) {

        Message message = null;
        try {
            message = DataSupport.where("mRegisterId=? and mType=?", registerId, "3").findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setRegisterId(registerId);
        message.setType(Message.TYPE_WELCOME);
        message.setTitle("你好");
        message.setContent("欢迎使用格格！");
        message.setTime(new Date().getTime());
        message.setImageResource(R.drawable.icon_zh);
        message.save();
    }
}
