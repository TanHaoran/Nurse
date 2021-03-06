package com.jerry.nurse.util;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerry on 2017/9/3.
 */

public class BottomDialogManager {

    private static final int LINE_HEIGHT = 46;

    private Context mContext;

    private Dialog mDialog;

    private OnItemSelectedListener mListener;

    private List<String> mItems;

    private TextView mTitleTextView;

    private String mCurrent;

    public void setOnItemSelectedListener
            (List<String> items, OnItemSelectedListener listener) {
        mItems = items;
        mListener = listener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position, String item);
    }

    public BottomDialogManager(Context context) {
        mContext = context;
    }

    public void showSelectDialog(String current) {
        mCurrent = current;
        mDialog = new Dialog(mContext, R.style.my_dialog);
        // 设置点击周围可以消失
        mDialog.setCanceledOnTouchOutside(true);

        LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.view_bottom_select, null);

        mTitleTextView = (TextView) root.findViewById(R.id.tv_title);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rv_data);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        RecyclerViewDecorationUtil.addItemDecoration(mContext, recyclerView);

        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        SelectAdapter adapter = new SelectAdapter(mContext, R.layout
                .item_string, mItems);
        recyclerView.setAdapter(adapter);

        mDialog.setContentView(root);

        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 添加动画
        dialogWindow.setWindowAnimations(R.style.dialogstyle);
        // 获取对话框当前的参数值
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // 设置x坐标
        lp.x = 0;
        // 设置y坐标
        lp.y = -20;
        // 设置宽度
        lp.width = mContext.getResources().getDisplayMetrics()
                .widthPixels;
        // 设置高度
        if (mItems.size() <= 6) {
            lp.height = (mItems.size() + 1) * DensityUtil.dp2px(mContext, LINE_HEIGHT);
        } else {
            lp.height = 6 * DensityUtil.dp2px(mContext, LINE_HEIGHT);
        }
        lp.height += DensityUtil.dp2px(mContext, 12);

        // 计算是否要显示空白的底布区域
        if (DensityUtil.hasNavBar(mContext)) {
            lp.height += DensityUtil.dp2px(mContext, DensityUtil.getNavigationBarHeight(mContext));
        }

        root.measure(0, 0);
        // 设置透明度
        lp.alpha = 9f;
        // 设置布局参数
        dialogWindow.setAttributes(lp);
        // 显示对话框
        mDialog.show();
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    class SelectAdapter extends CommonAdapter<String> {
        public SelectAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(final ViewHolder holder, final String item, final int position) {
            holder.setText(R.id.tv_string, item);
            if (item.equals(mCurrent)) {
                holder.setVisible(R.id.iv_choose, true);
            } else {
                holder.setVisible(R.id.iv_choose, false);
            }
            holder.setOnClickListener(R.id.ll_main, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemSelected(position, item);
                    }
                    mCurrent = item;
                    holder.setVisible(R.id.iv_choose, true);
                    notifyDataSetChanged();
                    mDialog.dismiss();
                }
            });
        }
    }
}
