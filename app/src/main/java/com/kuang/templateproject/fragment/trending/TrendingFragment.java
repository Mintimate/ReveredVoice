/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.kuang.templateproject.fragment.trending;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

import com.kuang.templateproject.core.BaseFragment;
import com.kuang.templateproject.utils.PermisstionUtil;
import com.kuang.templateproject.utils.XToastUtils;
import com.kuang.templateproject.utils.handle.MediaUtils;
import com.kuang.templateproject.utils.handle.PlayUtils;
import com.kuang.templateproject.utils.record.MyAudioManager;
import com.xuexiang.templateproject.R;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.progress.CircleProgressView;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;
import jaygoo.widget.wlv.WaveLineView;

/**
 * @author cxq
 * @since 2021-06-15 21:19
 */
@Page(anim = CoreAnim.present)
public class TrendingFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {
//    @BindView(R.id.speed)
//    TextView speed;
//    @BindView(R.id.seekBar_offset_x)
//    SeekBar offset_speed;
    @BindView(R.id.waveLineView)
    WaveLineView waveLineView;
    @BindView(R.id.play)
    Button playButton;
    @BindView(R.id.progressView_circle_small)
    CircleProgressView progressViewCircleSmall;
    @BindView(R.id.back)
    Button backButton;
    @BindView(R.id.forward)
    Button forwardButton;


    private static final int STATE_NORMAL = 1;// ???????????????
    private static final int STATE_RECORDING = 2;// ????????????
    private static final int STATE_WANT_TO_CANCEL = 3;// ????????????

    private int mCurrentState = STATE_NORMAL; // ???????????????
    private boolean isRecording = false;// ??????????????????
    private volatile  boolean exit = false;

    private static final int DISTANCE_Y_CANCEL = 50;

    private MyAudioManager mMyAudioManager;

    // ????????????longClick
    private boolean mReady;
    android.media.AudioManager audioManager;
    private MediaPlayer mediaPlayer;

    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGED = 0x111;
    private static final int MSG_DIALOG_DIMISS = 0x112;
    private static final int MSG_TIME_OUT = 0x113;
    private static final int UPDATE_TIME = 0x114;
    private static final int AUDIO_START = 0x115;
    private static final int AUDIO_PLAY = 0x116;
    private static final int AUDIO_PAUSE = 0x117;
    private static final int AUDIO_BACK = 0x118;
    private static final int AUDIO_FORWARD = 0x119;
    private static final int AUDIO_PROGRESS = 0x200;

    private boolean mThreadFlag = false;
    private int time = 0;
    private float mTime;
    private boolean isPlaying = false;
    private boolean recodingOver = false;
    private PlayUtils playUtils;
    private volatile boolean isChanging = false;

    /**
     * @return ????????? null????????????????????????
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }

    /**
     * ???????????????id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_trending;
    }

    /**
     * ???????????????
     */
    @Override
    protected void initViews() {
        init();


    }

//    ????????????????????????????????????
    private void init() {
        mediaPlayer = new MediaPlayer();
        playUtils = new PlayUtils(mediaPlayer);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

            PermisstionUtil.OnPermissionResult onPermissionResult = new PermisstionUtil.OnPermissionResult() {
                @Override
                public void granted(int requestCode) {

                }

                @Override
                public void denied(int requestCode) {

                }
            };
            PermisstionUtil.requestPermissions(getContext(), new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0, "????????????", onPermissionResult);
            Log.i("??????????????????", onPermissionResult.toString());
        }else {
            Log.i("??????????????????", "??????");
        }
    }

    // ????????????
    @OnTouch({R.id.play})
    public void onButtonTouch(View view,MotionEvent event){
        if(view.getId() == R.id.play){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(isPlaying) {
                        playButton.setBackground(getResources().getDrawable(R.drawable.ic_stop));
                        mHandler.sendEmptyMessage(AUDIO_PAUSE);
                        //????????????
                        isPlaying = false;
                    }else {
                        playButton.setBackground(getResources().getDrawable(R.drawable.ic_play));
                        //??????
                        mHandler.sendEmptyMessage(AUDIO_PLAY);
                        isPlaying = true;
                    }
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }


    @Override
    protected void initListeners() {
//        PlayerUtil.setPlayerCompletionListener(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("play","switch");
//                playButton.setBackground(getResources().getDrawable(R.drawable.ic_stop));
//                isPlaying = false;
//            }
//        });
        PlayUtils.setPlayerCompletionListener(new Runnable() {
            @Override
            public void run() {
                Log.i("play","switch");
                playButton.setBackground(getResources().getDrawable(R.drawable.ic_stop));
                isPlaying = false;
            }
        });
//        PlayerUtil.setPlayerFailRemind(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("PlayerUtils","fails");
//            }
//        });
        progressViewCircleSmall.setProgressViewUpdateListener(new CircleProgressView.CircleProgressUpdateListener() {
            @Override
            public void onCircleProgressStart(View view) {
                isChanging = false;

            }

            @Override
            public void onCircleProgressUpdate(View view, float progress) {
//                Timer mTimer = new Timer();
//                TimerTask mTimerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        if(isChanging==true) {
//                            return;
//                        }
//                        mHandler.sendEmptyMessage(AUDIO_PROGRESS);
//
//                    }
//                };
//                mTimer.schedule(mTimerTask, 0, 100);

                if(PlayUtils.getTotalProgress() !=0) {
                    BigDecimal bd1 = new BigDecimal(Double.toString(PlayUtils.getCurrentProgress() ));
                    BigDecimal bd2 = new BigDecimal(Double.toString(PlayUtils.getTotalProgress()));
                    double a = bd1.divide(bd2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double doubleValue = PlayUtils.getCurrentProgress() / PlayUtils.getTotalProgress();
                    progressViewCircleSmall.setProgress((float) (a));
                    Log.i("Progresstime", String.valueOf((float) (a)));
                }
            }

            @Override
            public void onCircleProgressFinished(View view) {
                isChanging = true;
                progressViewCircleSmall.setProgress((float)0.0);

            }
        });
        waveLineView.setLineColor(Color.BLUE);
        audioManager = (android.media.AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        String dir = Environment.getExternalStorageDirectory().getPath() + "/" + "AduioBack" + "/audio";

        mMyAudioManager = MyAudioManager.getInstance(dir);
        mMyAudioManager.setOnAudioStateListener(new MyAudioManager.AudioStateListener() {
            public void wellPrepared() {
                mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);//????????????
            }
        });
//        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                Log.i("play","switch");
//                playButton.setBackground(getResources().getDrawable(R.drawable.ic_stop));
//                isPlaying = false;
//            }
//        };

//        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if (seekBar == offset_speed) {
//                    java.text.DecimalFormat df = new java.text.DecimalFormat("0.0");
//                    String format = df.format(offset_speed.getProgress() * 0.2);
//                    speed.setText("??????????????????:" + format + "x");
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        };
//        offset_speed.setOnSeekBarChangeListener(onSeekBarChangeListener);
////        mediaPlayer.setOnCompletionListener(onCompletionListener);

    }

    @OnClick({R.id.start_audio,R.id.play,R.id.forward,R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.forward:
                mHandler.sendEmptyMessage(AUDIO_PLAY);
                break;
            case R.id.back:
                mHandler.sendEmptyMessage(AUDIO_BACK);
                break;
        }
    }


    @OnLongClick({R.id.start_audio})
    public void onLongClick(View view) {
            if (isPlaying){
                XToastUtils.toast("???????????????????????????");
            }else {
                mReady = true;
                mMyAudioManager.prepareAudio();
            }

    }

    @OnTouch({R.id.start_audio})
    public boolean onViewTouch(View view, MotionEvent event) {
        try {
            if (isPlaying) {

            } else {
//        int x = (int) event.getX();// ??????x?????????
                float last = 0;// ??????y?????????
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mThreadFlag = false;
                        changeState(STATE_RECORDING);
                        myRequestAudioFocus();
                        Log.i("play_audio", "??????");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.i("play_audio", "??????");
                        if (isRecording) {
                            // ???????????????????????????x,y??????????????????????????????
                            if (event.getY() < 0 && Math.abs(event.getY()) > 120) {
                                changeState(STATE_WANT_TO_CANCEL);
                            } else {
                                changeState(STATE_RECORDING);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        waveLineView.stopAnim();
                        Log.i("play_audio", "??????");
                        Log.i("play_audio", String.valueOf(mTime));
                        if (!mReady) {
                            reset();
                            return false;
                        }
                        if (!isRecording || mTime <= 1.0f) {//??????1???
                            Log.i("play_audio", "?????????");
                            XToastUtils.toast("?????????");
                            mMyAudioManager.cancel();
                            mHandler.sendEmptyMessage(MSG_DIALOG_DIMISS);//???????????????
                        } else if (mCurrentState == STATE_RECORDING) { // ??????????????????????????????
                            mMyAudioManager.release();
                            if (audioFinishRecorderListener != null) {
                                audioFinishRecorderListener.onFinish(mTime, mMyAudioManager.getCurrentFilePath());
                            }
                            mHandler.sendEmptyMessage(AUDIO_START);
                            recodingOver = true;
                        } else if (mCurrentState == STATE_WANT_TO_CANCEL) { // ????????????
                            mMyAudioManager.cancel();
                        }
                        reset();
                        audioManager.abandonAudioFocus(afChangeListener);
                        break;
                }
            }
        }
        catch (Exception e){
        }
        return false;
    }

    //?????????????????????????????????
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        public void run() {
            if(isRecording){
                waveLineView.startAnim();
            }
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    time++;
                    if (isWantToCancel) {
                    } else {
                        if (time % 10 == 0) {
                            mHandler.sendEmptyMessage(UPDATE_TIME);
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                    if (mTime >= 10.0f) {//??????????????????10????????????????????????
                        while (!mThreadFlag) {//????????????????????????????????????????????????????????????????????????
                            mMyAudioManager.release();
                            if (audioFinishRecorderListener != null) {
                                //?????????????????????????????????reset??????;
                                mHandler.sendEmptyMessage(MSG_TIME_OUT);
                                audioFinishRecorderListener.onFinish(mTime, mMyAudioManager.getCurrentFilePath());
                            }
                            mThreadFlag = !mThreadFlag;
                        }
                        isRecording = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    isRecording = true;
                    // ??????????????????
                    if(!isPlaying)
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:

                    break;
                case MSG_DIALOG_DIMISS:
                    break;
                case MSG_TIME_OUT://????????????
                    reset();
                    break;
                case UPDATE_TIME://????????????
                    if(time%10 == 0){
                        XToastUtils.toast(String.valueOf(time/10));
                    }
                    break;
                case AUDIO_START:
                    new Thread(handleAudio).start();
                    break;
                case AUDIO_PLAY:
                    if(!isRecording&&recodingOver&&mMyAudioManager!=null) {
                        if(!(mMyAudioManager.getCurrentFilePath()== null)) {
//                            audioPlay();
                            progressViewCircleSmall.startProgressAnimation();
                            playUtils.startVoice(mMyAudioManager.getCurrentFilePath());
                        }
                        else
                            Log.i("error","??????????????????");
                    }
                    break;
                case AUDIO_PAUSE:
                    if (!isRecording&&recodingOver){
//                        PlayerUtil.destroy();
                        progressViewCircleSmall.stopProgressAnimation();
                        progressViewCircleSmall.setProgress((float)0.0);
                        playUtils.stopVoice();
                    }
                    break;
                case AUDIO_PROGRESS:
                    progressViewCircleSmall.setProgress(PlayUtils.getCurrentProgress());
                    break;
                case AUDIO_BACK:
                    if(!(mMyAudioManager.getCurrentFilePath()== null)) {
                        audioBackPlay();
//                        progressViewCircleSmall.startProgressAnimation();
//                            playUtils.startVoice(mMyAudioManager.getCurrentFilePath());
                    }else {
                        Log.i("error","??????????????????");
                    }
                    break;
                case AUDIO_FORWARD:
                    if(!(mMyAudioManager.getCurrentFilePath()== null)) {
                        audioPlay();
//                        progressViewCircleSmall.startProgressAnimation();
                            playUtils.startVoice(mMyAudioManager.getCurrentFilePath());
                    }else {
                        Log.i("error","??????????????????");
                    }
                    break;

            }
            return false;
        }
    });

    private void audioBackPlay() {
        String currentFile = mMyAudioManager.getCurrentFilePath().substring(0,mMyAudioManager.getCurrentFilePath().lastIndexOf("/"));
//        PlayerUtil.continuePlay(new File(currentFile+"-back.mp3"));
        Log.i("audioBackPlay",currentFile+"/back/"+mMyAudioManager.getFile().getName());
        playUtils.startVoice(currentFile+"/back/"+mMyAudioManager.getFile().getName());
    }

    private void audioPlay() {
        Log.i("llll",mMyAudioManager.getCurrentFilePath());
        playUtils.pauseVoice();
    }


    private Runnable handleAudio = new Runnable() {
        @Override
        public void run() {
//            MediaUtils.reverseMedia(mMyAudioManager.getCurrentFilePath(),mMyAudioManager.getCurrentFilePath());
            String currentFile = mMyAudioManager.getCurrentFilePath().substring(0,mMyAudioManager.getCurrentFilePath().lastIndexOf("/"));
            MediaUtils.reverseMedia(mMyAudioManager.getCurrentFilePath(),currentFile+"/back/"+mMyAudioManager.getFile().getName());
            Log.i("????????????",currentFile+"/back/"+mMyAudioManager.getFile().getName());
        }
    };
    /**
     * ????????????????????????
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener audioFinishRecorderListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        audioFinishRecorderListener = listener;
    }

    android.media.AudioManager.OnAudioFocusChangeListener afChangeListener = new android.media.AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
            } else if (focusChange == android.media.AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
            } else if (focusChange == android.media.AudioManager.AUDIOFOCUS_LOSS) {
                audioManager.abandonAudioFocus(afChangeListener);
                // Stop playback
            }
        }
    };

    public void myRequestAudioFocus() {
        Log.i("play_audio", "????????????");
        audioManager.requestAudioFocus(afChangeListener, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }


    /**
     * ????????????????????????
     */
    private void reset() {
        isRecording = false;
        mTime = 0;
        time = 1;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancle(int x, int y) {
        if (y < 0) {
            return true;
        } else {
            return false;
        }
    }

    boolean isWantToCancel = false;

    /**
     * ??????
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    XToastUtils.toast("????????????");
                    break;
                case STATE_RECORDING:
                    XToastUtils.toast("????????????");
                    isWantToCancel = false;
                    break;
                case STATE_WANT_TO_CANCEL:
                    XToastUtils.toast("???????????????????????????");
                    isWantToCancel = true;
                    break;
            }
        }
    }
}
