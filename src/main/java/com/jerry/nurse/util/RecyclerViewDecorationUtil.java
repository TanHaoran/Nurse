package com.jerry.nurse.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jerry.nurse.R;
import com.jerry.nurse.view.RecycleViewDivider;

/**
 * Created by Jerry on 2017/8/27.
 */

public class RecyclerViewDecorationUtil {
    /**
     * 给RecyclerView添加水平间隔线
     *
     * @param context
     * @param recyclerView
     */
    public static void addItemDecoration(Context context,
                                         RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new RecycleViewDivider(context,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(context, 0.5f),
                context.getResources().getColor(R.color.divider_line)));
    }
}
