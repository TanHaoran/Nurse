package com.jerry.nurse.util;

import android.media.*;
import android.media.AudioManager;

import java.io.IOException;

/**
 * Created by Jerry on 2017/8/2.
 */

public class MediaManager {

    private static MediaPlayer sMediaPlayer;

    private static boolean sIsPause;

    /**
     * 播放音频
     *
     * @param filePath
     * @param onCompletionListener
     */
    public static void playSound(String filePath,
                                 MediaPlayer.OnCompletionListener onCompletionListener) {
        if (sMediaPlayer == null) {
            sMediaPlayer = new MediaPlayer();
            sMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    sMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            sMediaPlayer.reset();
        }

        try {
            sMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            sMediaPlayer.setOnCompletionListener(onCompletionListener);
            sMediaPlayer.setDataSource(filePath);
            sMediaPlayer.prepare();
            sMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (sMediaPlayer != null && sMediaPlayer.isPlaying()) {
            sMediaPlayer.pause();
            sIsPause = true;
        }
    }

    /**
     * 继续播放
     */
    public void resume() {
        if (sMediaPlayer != null && sIsPause) {
            sMediaPlayer.start();
            sIsPause = false;
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (sMediaPlayer != null) {
            sMediaPlayer.release();
            sMediaPlayer = null;
        }
    }
}
