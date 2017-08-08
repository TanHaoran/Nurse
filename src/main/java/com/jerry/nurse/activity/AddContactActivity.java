package com.jerry.nurse.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.listener.PermissionListener;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class AddContactActivity extends BaseActivity {

    @Bind(R.id.et_keyword)
    EditText mKeywordEditText;

    @Bind(R.id.tv_cancel)
    TextView mCancelTextView;

    @Bind(R.id.ll_add_way)
    LinearLayout mAddWayLayout;

    @Bind(R.id.rv_contact)
    RecyclerView mRecyclerView;


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, AddContactActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_add_contact;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = mKeywordEditText.getText().toString();
                    searchUser(keyword);
                }
                return false;
            }
        });
    }

    /**
     * 查找用户
     *
     * @param keyword
     */
    private void searchUser(String keyword) {
        mAddWayLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mCancelTextView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.ll_scan)
    void onScan(View view) {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);

    }

    @OnClick(R.id.ll_cellphone_contact)
    void onCellphoneContact(View view) {
        BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.READ_CONTACTS},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        Intent intent = CellphoneContactActivity.getIntent(AddContactActivity.this);
                        startActivity(intent);
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {

                    }
                });
    }

    @OnClick(R.id.tv_cancel)
    void onCancel(View view) {
        mAddWayLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setItemViewCacheSize(View.GONE);
        mCancelTextView.setVisibility(View.GONE);
        mKeywordEditText.setText("");
    }

}
