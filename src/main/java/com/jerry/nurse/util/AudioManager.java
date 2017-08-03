package com.jerry.nurse.util;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Jerry on 2017/8/2.
 */

public class AudioManager {

    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    private static AudioManager mInstance;

    private boolean mIsPrepared;

    public AudioManager(String dir) {
        mDir = dir;
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    public interface AudioStateListener {
        void wellPrepared();
    }

    public AudioStateListener mListener;

    public static AudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * 准备完毕时的回调方法
     *
     * @param listener
     */
    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    /**
     * 准备录音
     */
    public void prepareAudio() {
        try {
            mIsPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = generateFileName();
            File file = new File(dir, fileName);

            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            // 设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            // 设置麦克风为音频源
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            // 设置音频编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 准备
            mMediaRecorder.prepare();
            // 开始录音
            mMediaRecorder.start();
            // 准备就绪
            mIsPrepared = true;

            if (mListener != null) {
                mListener.wellPrepared();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机生成文件名
     *
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    /**
     * 获取音量等级
     *
     * @param maxLevel
     * @return
     */
    public int getVoiceLevel(int maxLevel) {
        if (mIsPrepared) {
            try {
                if (mMediaRecorder != null) {
                    // mMediaRecorder.getMaxAmplitude()值在1~32761之间
                    return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mMediaRecorder!= null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /**
     * 取消录音
     */
    public void cancel() {
        if (mMediaRecorder!= null) {
            release();
            if (mCurrentFilePath != null) {
                File file = new File(mCurrentFilePath);
                file.delete();
                mCurrentFilePath = null;
            }
        }
    }
}
