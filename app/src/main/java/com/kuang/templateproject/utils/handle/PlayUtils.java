package com.kuang.templateproject.utils.handle;

import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.util.Log;
import android.widget.Button;

import com.kuang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.R;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;

/**
 * startVoice：传入一个音频地址，开始播放
 * stopVoice：停止当前播放的音频
 * pauseVoice：暂停/继续当前音频
 */
    public class PlayUtils extends MediaPlayer implements  MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{
    //设定MediaPlayer对象的三种播放状态、正在播放 、暂停播放、
    private static final int MEDIAPLAYER_IS_PLAYING = 1;
    private static final int MEDIAPLAYER_IS_PAUSE = 2;
    private static final int MEDIAPLAYER_IS_STOP = 3;
    private static final int MEDIAPLAYER_NOT_Ready = 4;
    private static int MEDIAPLAYER_STATE = MEDIAPLAYER_IS_STOP; // 开始的时候MediaPlayer对象处于停止状态
    //设定MediaPlayer对象的播放速率
    private static final float SPEED_ONE=1f;
    private static final float SPEED_INIT=1f;
    private static float MEDIAPLAYER_SPEED=SPEED_INIT; //音频初始为1
    private volatile boolean exit = false;
    private static int currentProgress;
    private static int totalProgress;

    public PlayUtils() {
        setCurrentProgress();
    }

    public static void setPlayerCompletionListener(Runnable playerCompletionListener) {
        PlayUtils.playerCompletionListener = playerCompletionListener;
    }

    private static Runnable playerCompletionListener;

    // 一个媒体对象
    MediaPlayer mediaPlayer;


    public PlayUtils(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    /**
     * 核心方法-播放音频
     *
     * @param pathToVoiceFile：音频文件地址
     * @return
     */
    public boolean startVoice(String pathToVoiceFile) {
        initMediaPlayer(pathToVoiceFile);
        Log.i("播放",pathToVoiceFile);

        if (MEDIAPLAYER_STATE != MEDIAPLAYER_IS_STOP) {
            return false;
        }
        playMedia();
        setCurrentProgress();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 播放结束，释放播放器
                Log.i("audio","播放结束");
                stopVoice();
                if (playerCompletionListener != null) {
                    playerCompletionListener.run();
                }
            }
        });
        exit = false;

        return true;
    }

    public static int getCurrentProgress() {
        Log.i("Progress", String.valueOf(currentProgress));
        return currentProgress;
    }
    public static int getTotalProgress() {
        Log.i("Progress", String.valueOf(totalProgress));
        return totalProgress;
    }

    public  void setCurrentProgress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("process", String.valueOf(exit));
                if(!exit){
                    totalProgress = mediaPlayer.getDuration();
                }
                while (!exit){
                    currentProgress = mediaPlayer.getCurrentPosition();

                }
            }
        }).start();
    }
    /**
     * 次要方法-主动结束、销毁音频
     *
     * @return
     */
    public boolean stopVoice() {
        // 播放和暂停时，可以销毁
        if ((MEDIAPLAYER_STATE == MEDIAPLAYER_IS_PLAYING) || (MEDIAPLAYER_STATE == MEDIAPLAYER_IS_PAUSE)) {
            stopMedia();
            Log.i("播放","stop");
            return false;
        }
        return true;

    }

    /**
     * 次要方法-主动暂停音频
     *
     * @return
     */
    public boolean pauseVoice() {
        if (MEDIAPLAYER_STATE == MEDIAPLAYER_IS_STOP) {
            return false;
        }
        pauseMedia();
        return true;
    }

    /**
     * 次要方法-主动倍速+-0.5
     *
     * @return
     */

    public float addPlaySpeed(){
        if (MEDIAPLAYER_STATE==MEDIAPLAYER_IS_PLAYING){
            multipleSpeed("add");
        }
        return (float) MEDIAPLAYER_SPEED;
    }
    public float subPlaySpeed(){
        if (MEDIAPLAYER_STATE==MEDIAPLAYER_IS_PLAYING){
            multipleSpeed("sub");
        }
        return (float) MEDIAPLAYER_SPEED;
    }


    private void initMediaPlayer(String pathToVoiceFile) {
        if(MEDIAPLAYER_STATE==MEDIAPLAYER_IS_PLAYING){
            stopVoice();
        }
        Log.i("File",pathToVoiceFile);
        File file = new File(pathToVoiceFile);
        if (!file.exists()) {
            //更新状态为非就绪状态代码：4
            MEDIAPLAYER_STATE = MEDIAPLAYER_NOT_Ready;
        } else {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(pathToVoiceFile); // 设置播放的文件位置
//                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepare(); // 准备文件
                MEDIAPLAYER_STATE = MEDIAPLAYER_IS_STOP;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playMedia() // 开始播放
    {
        mediaPlayer.start();

        MEDIAPLAYER_STATE = MEDIAPLAYER_IS_PLAYING;
    }

    private void pauseMedia() // 暂停/继续播放
    {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            MEDIAPLAYER_STATE = MEDIAPLAYER_IS_PAUSE; // 更新播放状态
        }
        else if(MEDIAPLAYER_STATE==MEDIAPLAYER_IS_PAUSE){
            mediaPlayer.start();
            MEDIAPLAYER_STATE=MEDIAPLAYER_IS_PLAYING;
        }

    }

    private void stopMedia() // 停止播放
    {
        exit = true;
        mediaPlayer.reset();
        Log.i("pLayer","I am stopped");
        MEDIAPLAYER_STATE = MEDIAPLAYER_IS_STOP; // 更新播放状态
    }

    private boolean multipleSpeed(String flag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            float totalSPEED=1;
            if (flag.equals("add")){
                totalSPEED = MEDIAPLAYER_SPEED+SPEED_ONE;
            }
            else {
                totalSPEED = MEDIAPLAYER_SPEED-SPEED_ONE;
            }
            if(totalSPEED<=0){
                return false;
            }
            try {
                PlaybackParams params = mediaPlayer.getPlaybackParams();
                params.setSpeed(totalSPEED);
                mediaPlayer.setPlaybackParams(params);
                MEDIAPLAYER_SPEED = totalSPEED;
                return true;
            }catch (Exception e){
                Log.e("setPlaySpeed: ", String.valueOf(e));
            }
        }
        return false;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.i("play","Error"+what+extra);
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        this.stopMedia();

    }
}
