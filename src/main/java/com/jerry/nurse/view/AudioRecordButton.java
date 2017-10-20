package com.jerry.nurse.view;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.activity.BaseActivity;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.util.AudioManager;

import java.util.List;

/**
 * Created by Jerry on 2017/8/1.
 */

public class AudioRecordButton extends AppCompatButton implements AudioManager.AudioStateListener {

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    private static final int DISTANCE_Y = 50;

    private static final int MESSAGE_AUDIO_PREPARED = 0x110;
    private static final int MESSAGE_VOICE_CHANGED = 0x111;
    private static final int MESSAGE_DIALOG_DISMISS = 0x112;

    private int mCurrentState = STATE_NORMAL;

    private boolean mIsRecording = false;

    private DialogManager mDialogManager;

    private AudioManager mAudioManager;

    // 记录录音时间
    private float mTime;
    // 是否出发LongClick
    private boolean mReady;

    private AudioFinishRecordListener mListener;

    public void setAudioFinishRecordListener(AudioFinishRecordListener listener) {
        mListener = listener;
    }

    /**
     * 录音完成后的回调函数
     */
    public interface AudioFinishRecordListener {
        void onFinish(float seconds, String filePath);
    }

    // 获取音量大小的线程
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (mIsRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mHandler.sendEmptyMessage(MESSAGE_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_AUDIO_PREPARED:
                    mDialogManager.showRecordingDialog();
                    mIsRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MESSAGE_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MESSAGE_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
            }
        }
    };


    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDialogManager = new DialogManager(context);
        String dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();

        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }


    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MESSAGE_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        int action = event.getAction();
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                changeState(STATE_RECORDING);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                if (mIsRecording) {
                                    // 判断是否想要取消
                                    if (wantToCancel(x, y)) {
                                        changeState(STATE_WANT_TO_CANCEL);
                                    } else {
                                        changeState(STATE_RECORDING);
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                if (!mReady) {
                                    reset();
                                }
                                // 录音时间过短
                                if (!mIsRecording || mTime < 1.0f) {
                                    mDialogManager.tooShort();
                                    mAudioManager.cancel();
                                    mHandler.sendEmptyMessageDelayed(MESSAGE_DIALOG_DISMISS, 1300);
                                } else if (mCurrentState == STATE_RECORDING) {
                                    // 正常录制结束
                                    mDialogManager.dismissDialog();
                                    mAudioManager.release();
                                    // 执行回调函数
                                    if (mListener != null) {
                                        mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                                    }
                                } else if (mCurrentState == STATE_WANT_TO_CANCEL) {
                                    mDialogManager.dismissDialog();
                                    mAudioManager.cancel();
                                }
                                reset();
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {
                    }
                });

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
            setTextColor(ContextCompat.getColor(getContext(), R.color.normal_textColor));
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.record_button_normal);
                    setText(R.string.press_and_record);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.record_button_recording);
                    setText(R.string.up_and_cancel);
                    if (mIsRecording) {
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
     * 重置状态
     */
    private void reset() {
        mIsRecording = false;
        mReady = false;
        mIsRecording = false;
        changeState(STATE_NORMAL);
        mTime = 0;
    }

}
