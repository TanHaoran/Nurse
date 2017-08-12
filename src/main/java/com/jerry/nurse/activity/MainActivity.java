package com.jerry.nurse.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.jerry.nurse.R;
import com.jerry.nurse.fragment.ContactFragment;
import com.jerry.nurse.fragment.MeFragment;
import com.jerry.nurse.fragment.MessageFragment;
import com.jerry.nurse.fragment.OfficeFragment;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.util.EaseMobManager;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.jerry.nurse.R.string.contact;

public class MainActivity extends BaseActivity
        implements BottomNavigationBar.OnTabSelectedListener {

    public static final String ACTION_CHAT_MESSAGE_RECEIVE = "action_chat_message_receive";
    public static final String ACTION_FRIEND_APPLY_RECEIVE = "action_friend_apply_receive";

    public static final String EXTRA_CHAT_MESSAGE = "extra_chat_message";

    public static final String EXTRA_FRIEND_APPLY_CONTACT = "extra_friend_apply";

    @Bind(R.id.bnb_main)
    BottomNavigationBar mNavigationBar;

    private List<Fragment> mFragments;
    private MessageFragment mMessageFragment;
    private OfficeFragment mOfficeFragment;
    private ContactFragment mContactFragment;
    private MeFragment mMeFragment;
    private ProgressDialog mProgressDialogManager;

    private EaseMobManager mEaseMobManager;
    private MessageReceiver mMessageReceiver;

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
        mRegisterId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        // 环信登陆
        mEaseMobManager = new EaseMobManager(this);
        mEaseMobManager.login(mRegisterId);

        mProgressDialogManager = new ProgressDialog(this);
        // 设置导航栏按钮数据
        BottomNavigationItem messageItem = new BottomNavigationItem(
                R.drawable.ic_action_message,
                R.string.message);

        BottomNavigationItem officeItem = new BottomNavigationItem(R.drawable
                .ic_action_office, R.string.business);

        BottomNavigationItem contactItem = new BottomNavigationItem(R.drawable
                .ic_action_contact, contact);

        BottomNavigationItem mineItem = new BottomNavigationItem(R.drawable.ic_action_me, R
                .string.mine);

        // 添加元素并显示
        mNavigationBar.addItem(messageItem)
                .addItem(officeItem)
                .addItem(contactItem)
                .addItem(mineItem)
                .setFirstSelectedPosition(0).initialise();

        // 注册点击事件
        mNavigationBar.setTabSelectedListener(this);

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
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fl_main, mMessageFragment);
        transaction.commit();
    }


    @Override
    public void onTabSelected(int position) {
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

    @Override
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
    public void onTabReselected(int position) {
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
                ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra(EXTRA_CHAT_MESSAGE);

                // 发出Notification
                Intent newIntent = ChatActivity.getIntent(context, chatMessage.getFrom());
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
                if (mMessageFragment.isVisible()) {
                    mMessageFragment.refresh();
                    return;
                }
                L.i("接收到好友申请广播");
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


}
