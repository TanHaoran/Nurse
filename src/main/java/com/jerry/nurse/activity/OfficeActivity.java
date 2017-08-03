package com.jerry.nurse.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Office;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.UserUtil;
import com.jerry.nurse.view.RecycleViewDivider;
import com.jerry.nurse.view.TitleBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;


public class OfficeActivity extends BaseActivity {

    @Bind(R.id.tb_list)
    TitleBar mTitleBar;

    @Bind(R.id.rv_list)
    RecyclerView mRecyclerView;

    @BindString(R.string.office)
    String mTitle;

    private OfficeAdapter mAdapter;
    private ProgressDialog mProgressDialog;

    private UserHospitalInfo mUserHospitalInfo;

    private List<Office> mOffices;

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
        // 初始化等待框
        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("请稍后...");

        mTitleBar.setTitle(mTitle);
        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });

        mUserHospitalInfo = DataSupport.findFirst(UserHospitalInfo.class);

        getOfficeList(mUserHospitalInfo.getHospitalId());

    }

    /**
     * 根据医院Id获取科室列表
     *
     * @param hospitalId
     */
    private void getOfficeList(String hospitalId) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_OFFICE_LIST)
                .addParams("HospitalId", hospitalId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        try {
                            mOffices = new Gson().fromJson(response,
                                    new TypeToken<List<Office>>() {
                                    }.getType());
                            if (mOffices != null) {
                                setOfficeData();
                            }
                        } catch (JsonSyntaxException e) {
                            L.i("获取科室信息失败");
                            e.printStackTrace();
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
                postHospital(mOffices.get(position));
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
    private void postHospital(Office office) {
        final UserHospitalInfo userHospitalInfo = DataSupport.findFirst(UserHospitalInfo.class);
        userHospitalInfo.setDepartmentName(office.getName());
        userHospitalInfo.setDepartmentId(office.getDepartmentId());
        mProgressDialog.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_HOSPITAL_INFO)
                .content(StringUtil.addModelWithJson(userHospitalInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        if (response.equals(REQUEST_SUCCESS)) {
                            UserUtil.saveHospitalInfo(userHospitalInfo);
                            T.showShort(OfficeActivity.this, R.string.submit_success);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            L.i("保存失败");
                        }
                    }
                });
    }

    class OfficeAdapter extends CommonAdapter<Office> {

        public OfficeAdapter(Context context, int layoutId, List<Office> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Office office, int position) {
            ((TextView) holder.getView(R.id.tv_string)).setText(office.getName());
        }
    }
}
