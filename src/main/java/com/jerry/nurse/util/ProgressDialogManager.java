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

    public void setMessage(String message) {
        mProgressDialog.setMessage(message);
    }

    public void show() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void dismiss() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
