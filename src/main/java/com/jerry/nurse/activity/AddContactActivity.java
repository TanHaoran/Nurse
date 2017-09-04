package com.jerry.nurse.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.model.SearchUsersResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.RecycleViewDivider;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.jerry.nurse.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;
import static com.jerry.nurse.activity.CaptureActivity.RESULT_CODE_QR_SCAN;
import static com.jerry.nurse.constant.ServiceConstant.AVATAR_ADDRESS;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class AddContactActivity extends BaseActivity {

    private static final int REQUEST_SCAN_QR_CODE = 0x101;

    @Bind(R.id.et_keyword)
    EditText mKeywordEditText;

    @Bind(R.id.tv_cancel)
    TextView mCancelTextView;

    @Bind(R.id.ll_add_way)
    LinearLayout mAddWayLayout;

    @Bind(R.id.rv_contact)
    RecyclerView mRecyclerView;

    private List<SearchUsersResult.User> mUsers;
    private UserAdapter mAdapter;

    private ProgressDialogManager mProgressDialogManager;


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
        mProgressDialogManager = new ProgressDialogManager(this);
        mKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = mKeywordEditText.getText().toString();
                    if (!TextUtils.isEmpty(keyword)) {
                        getUserListByKeyWord(keyword);
                    } else {
                        T.showShort(AddContactActivity.this, "关键字不能为空");
                    }
                }
                return false;
            }
        });
    }

    /**
     * 根据关键字获取用户列表
     *
     * @param keyword
     */
    private void getUserListByKeyWord(String keyword) {

        mAddWayLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mCancelTextView.setVisibility(View.VISIBLE);

        mProgressDialogManager.show();
        String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        OkHttpUtils.get().url(ServiceConstant.SEARCH_USER_BY_KEY_WORD)
                .addParams("RegisterId", registerId)
                .addParams("KeyWord", keyword)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        SearchUsersResult searchUsersResult = new Gson().fromJson(response, SearchUsersResult.class);
                        if (searchUsersResult.getCode() == RESPONSE_SUCCESS) {
                            mUsers = searchUsersResult.getBody();
                            if (mUsers == null) {
                                mUsers = new ArrayList<>();
                            }
                            setUsersData();
                        } else {
                            T.showShort(AddContactActivity.this, searchUsersResult.getMsg());
                        }
                    }
                });
    }

    void setUsersData() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 设置间隔线
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));

        mAdapter = new UserAdapter(this, R.layout.item_user, mUsers);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                L.i("点击事件");
                String userRegisterId = mUsers.get(position).getRegisterId();
                Intent intent = ContactDetailActivity.getIntent(AddContactActivity.this, userRegisterId);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @OnClick(R.id.ll_scan)
    void onScan(View view) {
        BaseActivity.requestRuntimePermission(new String[]{Manifest
                        .permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(AddContactActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, REQUEST_SCAN_QR_CODE);
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN_QR_CODE && resultCode == RESULT_CODE_QR_SCAN) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString(INTENT_EXTRA_KEY_QR_SCAN);
            L.i("扫描结果是：" + result);
            String register = String.valueOf((Integer.parseInt(result) + 1) / 3);
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 10 - register.length();i++) {
                sb.append("0");
            }
            sb.append(register);
            L.i("解码后是：" + sb.toString());

            Intent intent = ContactDetailActivity.getIntent(AddContactActivity.this, sb.toString());
            startActivity(intent);

        }
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

    private class UserAdapter extends CommonAdapter<SearchUsersResult.User> {
        public UserAdapter(Context context, int layoutId, List<SearchUsersResult.User> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, SearchUsersResult.User user, int position) {
            holder.setText(R.id.tv_nickname, user.getNickName());
            holder.setText(R.id.tv_cellphone, user.getPhone());
            ImageView imageView = holder.getView(R.id.iv_avatar_arrow);
            if (!TextUtils.isEmpty(user.getAvatar())) {
                if (user.getAvatar().startsWith("http")) {
                    Glide.with(AddContactActivity.this).load(user.getAvatar()).into(imageView);
                } else {
                    Glide.with(AddContactActivity.this).load(AVATAR_ADDRESS + user.getAvatar()).into(imageView);
                }
            }
        }
    }
}
