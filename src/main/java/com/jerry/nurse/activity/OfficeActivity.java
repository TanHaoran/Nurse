package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.OfficeResult;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.model.UserInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.GUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LitePalUtil;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.view.RecycleViewDivider;
import com.jerry.nurse.view.TitleBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;


public class OfficeActivity extends BaseActivity {

    @Bind(R.id.tb_list)
    TitleBar mTitleBar;

    @Bind(R.id.rv_list)
    RecyclerView mRecyclerView;

    @BindString(R.string.office)
    String mTitle;

    private OfficeAdapter mAdapter;
    private ProgressDialogManager mProgressDialogManager;

    private UserHospitalInfo mUserHospitalInfo;

    private List<OfficeResult.Office> mOffices;

    private LoginInfo mLoginInfo;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, OfficeActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_list;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);

        mTitleBar.setTitle(mTitle);
        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });

        mLoginInfo = DataSupport.findFirst(LoginInfo.class);

        getOfficeList(mLoginInfo.getHospitalId());

    }

    /**
     * 根据医院Id获取科室列表
     *
     * @param hospitalId
     */
    private void getOfficeList(String hospitalId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_OFFICE_LIST)
                .addParams("HospitalId", hospitalId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        OfficeResult officeResult = new GUtil().fromJson(response, OfficeResult.class);
                        if (officeResult.getCode() == RESPONSE_SUCCESS) {
                            mOffices = officeResult.getBody();
                            if (mOffices == null) {
                                mOffices = new ArrayList<>();
                            }
                            setOfficeData();
                        } else {
                            L.i("获取科室信息失败");
                        }
                    }
                });
    }

    private void setOfficeData() {

        mAdapter = new OfficeAdapter(this, R.layout.item_string, mOffices);

        // 设置间隔线
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                postOffice(mOffices.get(position));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    /**
     * 更新医院信息
     *
     * @param office
     */
    private void postOffice(final OfficeResult.Office office) {
        UserHospitalInfo userHospitalInfo = new UserHospitalInfo();
        userHospitalInfo.setDepartmentName(office.getName());
        userHospitalInfo.setDepartmentId(office.getDepartmentId());
        userHospitalInfo.setRegisterId(mLoginInfo.getRegisterId());
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_HOSPITAL_INFO)
                .content(StringUtil.addModelWithJson(userHospitalInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("设置医院信息成功");
                            // 更新数据库
                            mLoginInfo.setDepartmentId(office.getDepartmentId());
                            mLoginInfo.setDepartmentName(office.getName());
                            LitePalUtil.updateLoginInfo(OfficeActivity.this, mLoginInfo);

                            UserInfo userInfo = DataSupport.findFirst(UserInfo.class);
                            userInfo.setDepartmentId(office.getDepartmentId());
                            userInfo.setDepartmentName(office.getName());
                            LitePalUtil.updateUserInfo(OfficeActivity.this, userInfo);

                            finish();
                        } else {
                            L.i("设置医院信息失败");
                        }
                    }
                });
    }

    class OfficeAdapter extends CommonAdapter<OfficeResult.Office> {

        public OfficeAdapter(Context context, int layoutId, List<OfficeResult.Office> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, OfficeResult.Office office, int position) {
            holder.setText(R.id.tv_string, office.getName());
            if (office.getDepartmentId().equals(mLoginInfo.getDepartmentId())) {
                holder.getView(R.id.iv_choose).setVisibility(View.VISIBLE);
            }else {
                holder.getView(R.id.iv_choose).setVisibility(View.INVISIBLE);
            }
        }
    }
}
