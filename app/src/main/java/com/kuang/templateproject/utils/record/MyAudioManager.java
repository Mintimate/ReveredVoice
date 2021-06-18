package com.kuang.templateproject.utils.record;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MyAudioManager {
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurrentFilePath;

    public File getFile() {
        return file;
    }

    private File file;

    private static MyAudioManager mInstance;

    private boolean isPrepare;

    private MyAudioManager(String dir) {
        mDir = dir;
    }

    public static MyAudioManager getInstance(String dir) {
        if (mInstance == null) {
            synchronized (MyAudioManager.class) {
                if (mInstance == null) {
                    mInstance = new MyAudioManager(dir);
                }
            }
        }
        return mInstance;
    }

    /**
     * 使用接口 用于回调
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    public AudioStateListener mAudioStateListener;

    /**
     * 回调方法
     */
    public void setOnAudioStateListener(AudioStateListener listener) {
        mAudioStateListener = listener;
    }

    // 去准备
    public void prepareAudio() {
        try {
            isPrepare = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            } else {
                if (!dir.isDirectory()) {
                    dir.delete();
                    dir.mkdirs();
                }
            }
            String fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            // 设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            // 设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 设置音频编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 准备录音
            mMediaRecorder.prepare();
            // 开始
            mMediaRecorder.start();
            // 准备结束
            isPrepare = true;
            if (mAudioStateListener != null) {
                mAudioStateListener.wellPrepared();
            }
            this.file = file;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){

        }
    }

    /**
     * 随机生成文件的名称
     */
    private String generateFileName() {
        String a[] = UUID.randomUUID().toString().split("-");
        return a[a.length-1].toString() + ".mp3";
    }

    public int getVoiceLevel(int maxlevel) {
        if (isPrepare) {
            try {
                return maxlevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {
            }
        }
        return 1;
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder = null;
        }
    }

    /**
     * 取消录音
     */
    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }
}
