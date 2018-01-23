package com.jerry.nurse.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
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
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.Message;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.EaseMobManager;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LitePalUtil;
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
    public static final String ACTION_CHAT_MESSAGE_READ = "action_chat_message_read";
    public static final String ACTION_FRIEND_APPLY_RECEIVE = "action_friend_apply_receive";

    public static final String EXTRA_CHAT_MESSAGE = "extra_chat_message";

    public static final String EXTRA_FRIEND_APPLY_CONTACT = "extra_friend_apply";

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

    private EaseMobManager mEaseMobManager;
    private MessageReceiver mMessageReceiver;

    private int mCurrentIndex = 0;

    private String mRegisterId;

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
        // 获取注册Id，进行环信登陆
        mRegisterId = (String) SPUtil.get(this, SPUtil
                .REGISTER_ID, "");
        mEaseMobManager = new EaseMobManager();
        mEaseMobManager.login(mRegisterId);

        // 判断是否是该账号首次登录，首次登录要显示欢迎信息
        boolean isFirstIn = (boolean) SPUtil.get(this, SPUtil.IS_FIRST_IN, true);
        if (isFirstIn) {
            // 创建首页欢迎信息
            createWelcomeMessage(mRegisterId);
        }
        SPUtil.put(this, SPUtil.IS_FIRST_IN, false);

        // 初始化Fragment
        getFragments();

        // 设置起始页
        setDefaultFragment();

        // 注册并开启广播监听
        mMessageReceiver = new MessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 1. 接收环信消息通知
        intentFilter.addAction(ACTION_CHAT_MESSAGE_RECEIVE);
        // 2. 接收环信添加好友通知
        intentFilter.addAction(ACTION_FRIEND_APPLY_RECEIVE);
        // 3. 接受已读消息回执通知
        intentFilter.addAction(ACTION_CHAT_MESSAGE_READ);
        registerReceiver(mMessageReceiver, intentFilter);

        // 更新最新的登录信息
        getLoginInfo(mRegisterId);
        // 获取好友列表，并更新本地数据库
        getFriendList(mRegisterId);
        // 获取群组列表，并更新本地数据库
        getGroupList(mRegisterId);
    }


    /**
     * 初始化Fragment
     */
    private void getFragments() {
        // 创建并添加4个Fragment页面
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

    /**
     * 4个TabMenu点击事件
     *
     * @param view
     */
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

    /**
     * TabMenu按钮选中事件
     *
     * @param position
     */
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

    /**
     * TabMenu清除选中事件
     *
     * @param position
     */
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

    /**
     * 屏蔽返回按钮，定义为按下Home键按钮
     */
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
        // 注销环信
        mEaseMobManager.logout();
        // 解除广播监听
        unregisterReceiver(mMessageReceiver);
    }

    /**
     * 广播接收器
     */
    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent newIntent;
            PendingIntent pi;
            Notification notification;
            NotificationManagerCompat notificationManager;
            ContactInfo info;
            switch (intent.getAction()) {
                // 收到消息
                case ACTION_CHAT_MESSAGE_RECEIVE:
                    L.i("接收到消息广播");
                    if (mMessageFragment.isVisible()) {
                        mMessageFragment.refresh();
                        return;
                    }
                    ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra(EXTRA_CHAT_MESSAGE);

                    // 群聊
                    if (chatMessage.isGroup()) {
                        newIntent = ChatActivity.getIntent(context, chatMessage.getTo(), true);
                    }
                    // 单聊
                    else {
                        newIntent = ChatActivity.getIntent(context, chatMessage.getFrom(), false);
                    }
                    pi = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // 读取这个人的个人信息
                    info = DataSupport.where("mRegisterId=?", chatMessage.getFrom()).findFirst(ContactInfo.class);

                    // 发出Notification
                    notification = new Notification.Builder(MainActivity.this)
                            .setTicker("新消息")
                            .setSmallIcon(R.drawable.topnew_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setContentTitle(info.getDisplayName())
                            .setContentText(chatMessage.getContent())
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .build();

                    notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(0, notification);

                    break;
                // 收到好友申请
                case ACTION_FRIEND_APPLY_RECEIVE:
                    L.i("接收好友申请广播");
                    if (mMessageFragment.isVisible()) {
                        mMessageFragment.refresh();
                        return;
                    }
                    AddFriendApply apply = (AddFriendApply) intent.getSerializableExtra(EXTRA_FRIEND_APPLY_CONTACT);

                    // 发出Notification
                    newIntent = AddContactApplyActivity.getIntent(context);

                    pi = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    // 读取这个人的个人信息
                    info = DataSupport.where("mRegisterId=?", apply.getContactId()).findFirst(ContactInfo.class);

                    notification = new Notification.Builder(MainActivity.this)
                            .setTicker("新消息")
                            .setSmallIcon(R.drawable.topnew_icon)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setContentTitle("好友申请")
                            .setContentText(info.getDisplayName() + "申请添加你为好友:" + apply.getReason())
                            .setContentIntent(pi)
                            .setAutoCancel(true)
                            .build();

                    notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(0, notification);
                    break;
                // 收到已读回执
                case ACTION_CHAT_MESSAGE_READ:
                    L.i("接收消息已读回执");
                    if (mMessageFragment.isVisible()) {
                        mMessageFragment.refresh();
                        return;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 获取用户登录信息，并更新本地数据库
     */
    private void getLoginInfo(final String registerId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_USER_LOGIN_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        // 如果登陆成功保存登陆信息并跳转页面
                        LoginInfoResult loginInfoResult = new Gson().fromJson(response, LoginInfoResult.class);
                        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
                            LitePalUtil.saveLoginInfo(MainActivity.this, loginInfoResult.getBody());
                        } else {
                            L.i("更新登录信息失败");
                        }
                    }
                });
    }

    /**
     * 获取用户好友资料，并更新本地数据库
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
                            if (contacts != null) {
//                                L.i("读取到联系人个数:" + contacts.size());
                                updateContactInfoData(contacts);
                            }
                        }
                    }
                });
    }

    /**
     * 获取群信息，并更新本地数据库
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
                            if (groupInfo != null) {
//                                L.i("初始化读取到了" + groupInfo.size() + "个群");
                                updateGroupInfoData(groupInfo);
                            }
                        }
                    }
                });
    }


    /**
     * 更新本地联系人数据(多条)
     *
     * @param contacts
     * @return 新增联系人的数量
     */
    public static int updateContactInfoData(List<Contact> contacts) {
        if (contacts == null) {
            return 0;
        }
        int count = 0;
        List<ContactInfo> localContacts = null;
        try {
            // 读取本地数据库好友个数
            localContacts = DataSupport.findAll(ContactInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (localContacts == null) {
            localContacts = new ArrayList<>();
        }
        for (Contact contact : contacts) {
            int i;
            for (i = 0; i < localContacts.size(); i++) {
                // 如果两个信息相同就更新
                ContactInfo localContact = localContacts.get(i);
                if (localContact.getRegisterId().equals(contact.getFriendId())) {
                    updateLocalContact(localContact, contact);
//                    L.i("更新一条联系人信息");
                    break;
                }
            }
            // 如果本地数据库没有就创建保存
            if (i == localContacts.size()) {
                ContactInfo localContact = new ContactInfo();
                updateLocalContact(localContact, contact);
                count++;
//                L.i("新增一条联系人信息");
            }
        }
        return count;
    }


    /**
     * 更新本地联系人数据(单条)
     *
     * @param contact
     * @return 更新的联系人数据
     */
    public static ContactInfo updateContactInfoData(Contact contact) {
        // 读取本地数据库联系人
        List<ContactInfo> localContacts = null;
        try {
            localContacts = DataSupport.findAll(ContactInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (localContacts == null) {
            localContacts = new ArrayList<>();
        }
        int i;
        for (i = 0; i < localContacts.size(); i++) {
            // 如果两个信息相同就更新
            ContactInfo localContact = localContacts.get(i);
            if (localContact.getRegisterId().equals(contact.getFriendId())) {
//                L.i("更新一条联系人信息");
                return updateLocalContact(localContact, contact);
            }
        }
        // 如果本地数据库没有就创建保存
        if (i == localContacts.size()) {
            ContactInfo localContact = new ContactInfo();
            return updateLocalContact(localContact, contact);
        }
        return null;
    }

    /**
     * 更新数据库联系人信息
     *
     * @param oldContact
     * @param newContact
     */
    private static ContactInfo updateLocalContact(ContactInfo oldContact,
                                                  Contact newContact) {
        LoginInfo loginInfo = DataSupport.findFirst(LoginInfo.class);
        oldContact.setMyId(loginInfo.getRegisterId());
        oldContact.setAvatar(newContact.getAvatar());
        oldContact.setName(newContact.getName());
        oldContact.setNickName(newContact.getNickName());
        oldContact.setCellphone(newContact.getPhone());
        oldContact.setRemark(newContact.getRemark());
        oldContact.setRegisterId(newContact.getFriendId());
        oldContact.setFriend(newContact.isFriend());
        oldContact.save();
        return oldContact;
    }


    /**
     * 更新本地群信息数据 (多条)
     *
     * @param groups
     * @return 新增的信息数量
     */
    public static int updateGroupInfoData(List<GroupInfo> groups) {
        int count = 0;
        List<GroupInfo> localGroups = null;
        try {
            localGroups = DataSupport.findAll(GroupInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (localGroups == null) {
            localGroups = new ArrayList<>();
        }
        for (GroupInfo group : groups) {
            // 首先更新该群的所有联系人信息
            if (group.getGroupMemberList() != null) {
                updateContactInfoData(group.getGroupMemberList());
            }
            int i;
            for (i = 0; i < localGroups.size(); i++) {
                // 如果两个信息相同就更新
                GroupInfo localGroup = localGroups.get(i);
                if (localGroup.getHXGroupId().equals(group.getHXGroupId())) {
                    updateLocalGroup(localGroup, group);
                    // 更新联系人信息
//                    L.i("更新了一条群信息");
                    break;
                }
            }
            // 如果本地数据库没有就创建保存
            if (i == localGroups.size()) {
                GroupInfo localGroup = new GroupInfo();
                updateLocalGroup(localGroup, group);
//                L.i("新增了一条群信息");
                count++;
            }
        }
        return count;
    }


    /**
     * 更新本地群信息数据(单条)
     *
     * @param group
     */
    public static GroupInfo updateGroupInfoData(GroupInfo group) {

        List<GroupInfo> localGroups = null;
        try {
            localGroups = DataSupport.findAll(GroupInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (localGroups == null) {
            localGroups = new ArrayList<>();
        }
        // 首先更新该群的所有联系人信息
        if (group.getGroupMemberList() != null) {
            updateContactInfoData(group.getGroupMemberList());
        }
        int i;
        for (i = 0; i < localGroups.size(); i++) {
            // 如果两个信息相同就更新
            GroupInfo localGroup = localGroups.get(i);
            if (localGroup.getHXGroupId().equals(group.getHXGroupId())) {
                return updateLocalGroup(localGroup, group);
            }
        }
        // 如果本地数据库没有就创建保存
        if (i == localGroups.size()) {
            GroupInfo localGroup = new GroupInfo();
            return updateLocalGroup(localGroup, group);
        }
        return null;
    }

    /**
     * 更新数据库群信息
     *
     * @param oldGroup
     * @param newGroup
     * @return
     */
    private static GroupInfo updateLocalGroup(GroupInfo oldGroup, GroupInfo
            newGroup) {
        oldGroup.setHXGroupId(newGroup.getHXGroupId());
        oldGroup.setHXNickName(newGroup.getHXNickName());
        oldGroup.setRegisterId(newGroup.getRegisterId());
        oldGroup.setCreateTime(newGroup.getCreateTime());
        oldGroup.setGroupUserCount(newGroup.getGroupUserCount());
        oldGroup.save();
        return oldGroup;
    }


    /**
     * 创建欢迎消息
     */
    private void createWelcomeMessage(String registerId) {
        Message message = null;
        try {
            message = DataSupport.where("mRegisterId=? and mType=?",
                    registerId, "3").findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setRegisterId(registerId);
        message.setType(Message.TYPE_WELCOME);
        message.setTitle("你好，欢迎回来！");
        message.setContent("感谢使用燕尾帽！");
        message.setTime(new Date().getTime());
        message.setImageResource(R.drawable.icon_zh);
        message.save();
    }
}
