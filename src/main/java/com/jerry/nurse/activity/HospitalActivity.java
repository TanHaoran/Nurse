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
import com.jerry.nurse.model.HospitalResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.model.UserInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.BaiduLocationManager;
import com.jerry.nurse.util.DensityUtil;
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


public class HospitalActivity extends BaseActivity {

    @Bind(R.id.tb_list)
    TitleBar mTitleBar;

    @Bind(R.id.rv_list)
    RecyclerView mRecyclerView;

    @BindString(R.string.hospital)
    String mTitle;

    private List<HospitalResult.Hospital> mHospitals;

    private HospitalAdapter mAdapter;


    private LoginInfo mLoginInfo;
    private BaiduLocationManager mLocationManager;


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, HospitalActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_list;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);

        mLoginInfo = DataSupport.findFirst(LoginInfo.class);

        mTitleBar.setTitle(mTitle);
        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });


        mLocationManager = new BaiduLocationManager(this);
        mLocationManager.setLocationListener(new BaiduLocationManager.LocationListener() {
            @Override
            public void onLocationFinished(double latitude, double longitude) {
                // 获取所有医院信息
                getNearbyHospital(String.valueOf(latitude), String.valueOf(longitude));
            }
        });
        mLocationManager.start();
    }


    /**
     * 获取附近的医院
     *
     * @param lat
     * @param lng
     */
    private void getNearbyHospital(String lat, String lng) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_NEARBY_HOSPITAL_LIST)
                .addParams("lat", lat)
                .addParams("lng", lng)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        HospitalResult hospitalResult = new Gson().fromJson(response, HospitalResult.class);
                        if (hospitalResult.getCode() == RESPONSE_SUCCESS) {
                            mHospitals = hospitalResult.getBody();
                            if (mHospitals == null) {
                                mHospitals = new ArrayList<>();
                            }
                            setHospitalData();
                        } else {
                            L.i("获取附近医院失败");
                        }
                    }
                });
    }

    /**
     * 设置医院列表数据
     */
    private void setHospitalData() {

        mAdapter = new HospitalAdapter(this, R.layout.item_string, mHospitals);

        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                postHospital(mHospitals.get(position));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        // 设置间隔线
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 更新医院信息
     *
     * @param hospital
     */
    private void postHospital(final HospitalResult.Hospital hospital) {
        UserHospitalInfo userHospitalInfo = new UserHospitalInfo();
        userHospitalInfo.setHospitalId(hospital.getHospitalId());
        userHospitalInfo.setHospitalName(hospital.getName());
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
                            mLoginInfo.setHospitalId(hospital.getHospitalId());
                            mLoginInfo.setHospitalName(hospital.getName());
                            LitePalUtil.updateLoginInfo(HospitalActivity.this, mLoginInfo);

                            UserInfo userInfo = DataSupport.findFirst(UserInfo.class);
                            userInfo.setHospitalId(hospital.getHospitalId());
                            userInfo.setHospitalName(hospital.getName());
                            LitePalUtil.updateUserInfo(HospitalActivity.this, userInfo);

                            finish();
                        } else {
                            L.i("设置医院信息失败");
                        }
                    }
                });
    }

    class HospitalAdapter extends CommonAdapter<HospitalResult.Hospital> {


        public HospitalAdapter(Context context, int layoutId, List<HospitalResult.Hospital> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, HospitalResult.Hospital hospital, int position) {
            holder.setText(R.id.tv_string, hospital.getName());
            if (hospital.getHospitalId().equals(mLoginInfo.getHospitalId())) {
                holder.getView(R.id.iv_choose).setVisibility(View.VISIBLE);
            } else {
                holder.getView(R.id.iv_choose).setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocationManager.isStarted()) {
            mLocationManager.stop();
        }
    }
}
