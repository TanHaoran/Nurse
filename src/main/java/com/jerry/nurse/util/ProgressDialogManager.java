package com.jerry.nurse.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.jerry.nurse.R;

/**
 * Created by Jerry on 2017/8/4.
 */

public class ProgressDialogManager {

    private ProgressDialog mProgressDialog;

    public ProgressDialogManager(Context context) {
        // 初始化等待框
        mProgressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("请稍后...");
    }

    /**
     * 显示对话框
     */
    public void show() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * 显示对话框
     *
     * @param message
     */
    public void show(String message) {
        mProgressDialog.setMessage(message);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * 隐藏对话框
     */
    public void dismiss() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
