package com.jerry.nurse.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerry.nurse.R;

/**
 * Created by Jerry on 2017/8/2.
 */

public class DialogManager {

    private Dialog mDialog;

    private ImageView mRecordImageView;
    private ImageView mVoiceImageView;

    private TextView mLableTextView;

    private Context mContext;

    public DialogManager(Context context) {
        mContext = context;
    }

    /**
     * 显示Dialog
     */
    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.view_dialog_recorder, null);
        mDialog.setContentView(view);

        mRecordImageView = (ImageView) mDialog.findViewById(R.id.iv_record);
        mVoiceImageView = (ImageView) mDialog.findViewById(R.id.iv_voice);
        mLableTextView = (TextView) mDialog.findViewById(R.id.tv_record);

        mDialog.show();
    }

    /**
     * 正在录音界面显示
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mRecordImageView.setVisibility(View.VISIBLE);
            mVoiceImageView.setVisibility(View.VISIBLE);
            mLableTextView.setVisibility(View.VISIBLE);

            mRecordImageView.setImageResource(R.drawable.recorder);
            mLableTextView.setText(R.string.up_and_cancel);
        }
    }

    /**
     * 取消录音
     */
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mRecordImageView.setVisibility(View.VISIBLE);
            mVoiceImageView.setVisibility(View.GONE);
            mLableTextView.setVisibility(View.VISIBLE);

            mRecordImageView.setImageResource(R.drawable.cancel);
            mLableTextView.setText(R.string.loosen_and_cancel);
        }
    }

    /**
     * 录音时间过短
     */
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mRecordImageView.setVisibility(View.VISIBLE);
            mVoiceImageView.setVisibility(View.GONE);
            mLableTextView.setVisibility(View.VISIBLE);

            mRecordImageView.setImageResource(R.drawable.voice_to_short);
            mLableTextView.setText(R.string.voice_too_short);
        }
    }

    /**
     * 关闭Dialog
     */
    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }


    /**
     * 通过level更新声音图片
     *
     * @param level
     */
    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
            mRecordImageView.setVisibility(View.VISIBLE);
            mVoiceImageView.setVisibility(View.GONE);
            mLableTextView.setVisibility(View.VISIBLE);

            int resId = mContext.getResources().getIdentifier("v" + level,
                    "drawable", mContext.getPackageName());
            mVoiceImageView.setImageResource(resId);
        }
    }

}

