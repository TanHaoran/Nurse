package com.jerry.nurse.view;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jerry.nurse.R;

/**
 * Created by Jerry on 2017/8/1.
 */

public class AudioRecordButton extends AppCompatButton {

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    private static final int DISTANCE_Y = 50;

    private int mCurrentState = STATE_NORMAL;

    private boolean isRecording = false;

    private DialogManager mDialogManager;


    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDialogManager = new DialogManager(context);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mDialogManager.showRecordingDialog();
                isRecording = true;
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // TODO
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    // 判断是否想要取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentState == STATE_RECORDING) {
                    mDialogManager.dismissDialog();
                } else if (mCurrentState == STATE_WANT_TO_CANCEL) {
                    mDialogManager.dismissDialog();
                }
                reset();
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 改变按钮状态
     *
     * @param state
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.record_button_normal);
                    setText(R.string.press_and_record);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.record_button_recording);
                    setText(R.string.up_and_cancel);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.record_button_recording);
                    setText(R.string.loosen_and_cancel);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    /**
     * 是否想要取消录音
     *
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        // 超出范围就取消
        if (y < -DISTANCE_Y || y > getHeight() + DISTANCE_Y) {
            return true;
        }
        return false;
    }

    /**
     * 恢复按钮状态
     */
    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
    }


}
