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
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.RecyclerViewDecorationUtil;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;
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
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class AddContactActivity extends BaseActivity {

    // 扫描二维吗的请求码
    private static final int REQUEST_SCAN_QR_CODE = 0x101;

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
        // 设置关键字搜索监听
        mKeywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = mKeywordEditText.getText().toString();
                    if (!TextUtils.isEmpty(keyword)) {
                        // 关键字不为空的时候，进行搜索查询
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
        // 隐藏操作列表
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
                        SearchUsersResult result = new Gson().fromJson(response, SearchUsersResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            List<SearchUsersResult.User> users =
                                    result.getBody();
                            if (users == null) {
                                users = new ArrayList<>();
                            }
                            setUsersData(users);
                        } else {
                            T.showShort(AddContactActivity.this, result.getMsg());
                        }
                    }
                });
    }

    /**
     * 填充数据
     *
     * @param users
     */
    void setUsersData(final List<SearchUsersResult.User> users) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 设置间隔线
        RecyclerViewDecorationUtil.addItemDecoration(this, mRecyclerView);


        UserAdapter adapter = new UserAdapter(this, R.layout.item_user,
                users);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                // 点击跳转到这个人的详细页面
                String userRegisterId = users.get(position).getRegisterId();
                Intent intent = ContactDetailActivity.getIntent(AddContactActivity.this, userRegisterId);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    /**
     * 二维码添加好友
     *
     * @param view
     */
    @OnClick(R.id.ll_scan)
    void onScan(View view) {
        // 申请权限
        BaseActivity.requestRuntimePermission(new String[]{Manifest
                        .permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        // 跳转二维码扫描窗口
                        Intent intent = new Intent(AddContactActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, REQUEST_SCAN_QR_CODE);
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {

                    }
                });
    }

    /**
     * 手机联系人添加好友
     *
     * @param view
     */
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN_QR_CODE && resultCode == RESULT_CODE_QR_SCAN) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString(INTENT_EXTRA_KEY_QR_SCAN);
            L.i("扫描结果是：" + result);
            String register = String.valueOf((Integer.parseInt(result) + 1) / 3);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10 - register.length(); i++) {
                sb.append("0");
            }
            sb.append(register);
            L.i("解码后是：" + sb.toString());

            Intent intent = ContactDetailActivity.getIntent(AddContactActivity.this, sb.toString());
            startActivity(intent);

        }
    }

    /**
     * 取消关键字搜索
     *
     * @param view
     */
    @OnClick(R.id.tv_cancel)
    void onCancel(View view) {
        // 隐藏关键字，显示操作列表
        mAddWayLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
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
            Glide.with(AddContactActivity.this).load(user.getAvatar()).into(imageView);
        }
    }
}
