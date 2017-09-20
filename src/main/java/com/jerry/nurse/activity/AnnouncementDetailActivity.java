package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.model.Announcement;
import com.jerry.nurse.util.DateUtil;

import butterknife.Bind;

public class AnnouncementDetailActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView mTitleTextView;

    @Bind(R.id.tv_institution)
    TextView mInstitutionTextView;

    @Bind(R.id.tv_time)
    TextView mTimeTextView;

    @Bind(R.id.tv_content)
    TextView mContentTextView;

    public static final String EXTRA_ANNOUNCEMENT = "extra_announcement";

    public static Intent getIntent(Context context, Announcement announcement) {
        Intent intent = new Intent(context, AnnouncementDetailActivity.class);
        intent.putExtra(EXTRA_ANNOUNCEMENT, announcement);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_announcement_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        Announcement announcement =
                (Announcement) getIntent().getSerializableExtra(EXTRA_ANNOUNCEMENT);
        if (announcement != null) {
            // 显示数据
            mTitleTextView.setText(announcement.getTitle());
            mInstitutionTextView.setText(announcement.getAgency());
            mTimeTextView.setText(DateUtil.parseMysqlDateToString(announcement.getNoticeTime()));
            mContentTextView.setText(announcement.getContent());
        }

        mContentTextView.setText(Html.fromHtml(announcement.getContent()));
    }
}
