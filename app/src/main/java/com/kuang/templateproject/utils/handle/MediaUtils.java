package com.kuang.templateproject.utils.handle;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;


public class MediaUtils {
    private static int RETURN_CODE_CANCEL = 1;
    private static int RETURN_CODE_SUCCESS = 0;
    private static int RETURN_STATE=RETURN_CODE_CANCEL;

    /**
     * 音频倒置（include：Voice、Video）
     *
     * @param inputPath：输入音频目录地址（如：～/Downloads/Summer.acc）
     * @param outputPath：输出音频目录地址（如：～/Downloads/SummerReversed.mp3）
     * @return
     */
    // 音频反转
    public static int reverseMedia(String inputPath, String outputPath) {
        init();
        // 这里拼接要注意空格
        String text = "ffmpeg -y -i " + inputPath + " -vf reverse -af areverse -preset superfast " + outputPath;
        String[] commands = text.split(" ");
        try {
            MyRxFFmpegSubscriber myRxFFmpegSubscriber = new MyRxFFmpegSubscriber();
            RxFFmpegInvoke.
                    getInstance().
                    runCommand(commands, null);
        }
        catch (Exception e){
            Log.println(1,"出错啦：",e.getMessage());
            e.printStackTrace();
        }
        return RETURN_STATE;
    }

    private static class MyRxFFmpegSubscriber extends RxFFmpegSubscriber {

        @Override
        public void onFinish() {
            RETURN_STATE=RETURN_CODE_SUCCESS;
        }

        @Override
        public void onProgress(int progress, long progressTime) {
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(String message) {
        }
    }

//    public static int reverseMedia(String inputPath, String outputPath) {
//        init();
//        int rc = FFmpeg.execute("-i " + inputPath + " -vf reverse -af areverse -preset superfast " + outputPath);
//        if (rc == RETURN_CODE_SUCCESS) {
//            Log.i(Config.TAG, "FFmpeg转码完成");
//            return RETURN_CODE_CANCEL;
//        } else if (rc == RETURN_CODE_CANCEL) {
//            Log.i(Config.TAG, "FFmpeg转码过程被用户取消操作");
//            return RETURN_CODE_CANCEL;
//        } else {
//            Log.i(Config.TAG, String.format("FFmpeg操作失败，返回值rc=%d", rc));
//            Config.printLastCommandOutput(Log.INFO);
//        }
//        return RETURN_CODE_SUCCESS;
//    }

    private static void init() {
        String dir1 = Environment.getExternalStorageDirectory().getPath() + "/" + "AduioBack" + "/audio/back";
        File dir = new File(dir1);
        if (!dir.exists()) {
            dir.mkdirs();
        } else {
            if (!dir.isDirectory()) {
                dir.delete();
                dir.mkdirs();
            }
        }

    }
}
