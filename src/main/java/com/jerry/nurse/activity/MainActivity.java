package com.jerry.nurse.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.jerry.nurse.R;
import com.jerry.nurse.fragment.ContactFragment;
import com.jerry.nurse.fragment.MeFragment;
import com.jerry.nurse.fragment.MessageFragment;
import com.jerry.nurse.fragment.OfficeFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MainActivity extends BaseActivity
        implements BottomNavigationBar.OnTabSelectedListener {

    @Bind(R.id.bnb_main)
    BottomNavigationBar mNavigationBar;

    private List<Fragment> mFragments;
    private MessageFragment mMessageFragment;
    private OfficeFragment mOfficeFragment;
    private ContactFragment mContactFragment;
    private MeFragment mMeFragment;
    private ProgressDialog mProgressDialogManager;

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
        mProgressDialogManager = new ProgressDialog(this);
        // 设置导航栏按钮数据
        BottomNavigationItem messageItem = new BottomNavigationItem(
                R.drawable.ic_action_message,
                R.string.message);

        BottomNavigationItem officeItem = new BottomNavigationItem(R.drawable
                .ic_action_office, R.string.business);

        BottomNavigationItem contactItem = new BottomNavigationItem(R.drawable
                .ic_action_contact, R.string.contact);

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
}
