package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.fragment.OfficeFragment;
import com.jerry.nurse.model.Announcement;
import com.jerry.nurse.model.AnnouncementsResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.ViewHolder;
import com.jerry.nurse.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;
import static com.jerry.nurse.fragment.OfficeFragment.DEFAULT_PAGE;

public class AnnouncementActivity extends BaseActivity {

    @Bind(R.id.rv_announcement)
    XRecyclerView mRecyclerView;

    private AnnouncementAdapter mAdapter;

    private List<Announcement> mAnnouncements;

    private ProgressDialogManager mProgressDialogManager;

    private LoginInfo mLoginInfo;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, AnnouncementActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_announcement;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);
        // 设置间隔线
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));
        mAnnouncements = new ArrayList<>();
        mAdapter = new AnnouncementAdapter(this, R.layout.item_announcement, mAnnouncements);
        mRecyclerView.setAdapter(mAdapter);

        // 获取公告数据
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        getAnnouncement(DEFAULT_PAGE, mLoginInfo.getHospitalId(), mLoginInfo.getDepartmentId());
    }

    /**
     * 获取公告列表咨询
     *
     * @param page
     * @param hospitalId
     * @param officeId
     */
    private void getAnnouncement(int page, String hospitalId, String officeId) {
        if (!OfficeFragment.checkPermission()) {
            hospitalId = "";
            officeId = "";
        } else {
            if (hospitalId == null) {
                hospitalId = "";
            }
            if (officeId == null) {
                officeId = "";
            }
        }
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_ANNOUNCEMENT)
                .addParams("pageNumber", String.valueOf(page))
                .addParams("HospitalId", hospitalId)
                .addParams("DepartmentId", officeId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    protected void onFilterError(Call call, Exception e, int id) {
                        //从数据库中获取数据
                        mAnnouncements = DataSupport.findAll(Announcement.class);
                        if (mAnnouncements != null) {
                            updateAnnouncements();
                        }
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        AnnouncementsResult result = new Gson().fromJson(response, AnnouncementsResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mAnnouncements = result.getBody();
                            if (mAnnouncements == null) {
                                mAnnouncements = new ArrayList<>();
                            }
                            updateAnnouncements();
                            if (mAnnouncements.size() > 0) {
                                //添加新数据到数据库
                                DataSupport.deleteAll(Announcement.class);
                                DataSupport.saveAll(result.getBody());
                            }
                        } else {
                            L.i(result.getMsg());
                        }
                    }
                });
    }

    /**
     * 更新公告列表显示
     */
    private void updateAnnouncements() {
        mAdapter.setDatas(mAnnouncements);
        mAdapter.notifyDataSetChanged();
    }

    class AnnouncementAdapter extends CommonAdapter<Announcement> {

        public AnnouncementAdapter(Context context, int layoutId, List<Announcement> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final Announcement announcement) {
            holder.setText(R.id.tv_title, announcement.getTitle());
            holder.setText(R.id.tv_institution, announcement.getAgency());
            holder.setText(R.id.tv_time, DateUtil.parseMysqlDateToString(announcement.getNoticeTime()));
            holder.getView(R.id.rl_announcement).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = AnnouncementDetailActivity.getIntent(AnnouncementActivity.this, announcement);
                    startActivity(intent);
                }
            });
        }

    }
}
