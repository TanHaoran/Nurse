package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jerry.nurse.R;
import com.jerry.nurse.util.ContactNumberUtil;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.PhoneInfo;
import com.jerry.nurse.view.RecycleViewDivider;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import butterknife.Bind;

public class CellphoneContactActivity extends BaseActivity {

    @Bind(R.id.rv_contact)
    RecyclerView mRecyclerView;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CellphoneContactActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_cellphone_contact;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        List<PhoneInfo> phoneNumberFromMobile = ContactNumberUtil.getPhoneNumberFromMobile(this);
        // 设置间隔线
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));

        CellphoneContactAdapter mAdapter = new CellphoneContactAdapter(this, R.layout.item_string, phoneNumberFromMobile);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setAdapter(mAdapter);
    }

    class CellphoneContactAdapter extends CommonAdapter<PhoneInfo> {

        public CellphoneContactAdapter(Context context, int layoutId, List<PhoneInfo> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, PhoneInfo phoneInfo, int position) {
            holder.setText(R.id.tv_string, phoneInfo.getName() + phoneInfo.getNumber());
        }
    }

}
