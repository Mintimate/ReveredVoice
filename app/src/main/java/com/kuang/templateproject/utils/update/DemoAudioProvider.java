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

package com.kuang.templateproject.utils.update;

import android.os.Environment;
import android.util.Log;

import com.kuang.templateproject.adapter.entity.AudioFile;
import com.kuang.templateproject.utils.Date.DateTimeUtil;
import com.kuang.templateproject.utils.handle.AudioFileUtils;
//import com.umeng.commonsdk.debug.D;
import com.xuexiang.xaop.annotation.MemoryCache;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DemoAudioProvider {
    public static String[] titles = new String[]{
            "",
            "无心法师:生死离别!月牙遭虐杀",
            "花千骨:尊上沦为花千骨",
            "综艺饭:胖轩偷看夏天洗澡掀波澜",
            "碟中谍4:阿汤哥高塔命悬一线,超越不可能",
    };

    // 首页轮播图
    public static String[] urls = new String[]{//640*360 360/640=0.5625
//            "https://host.mintimate.cn/imageHost/getImage/MTQwNTEwNDA1NzE4NTU1MDMzNw==",
            "https://host.mintimate.cn/imageHost/getImage/MTQwNTUzNDUzODY0NTM3NzAyNQ==",
            "https://host.mintimate.cn/imageHost/getImage/MTQwNTUzNDc3ODQwODU3MDg4Mg==",
            "https://host.mintimate.cn/imageHost/getImage/MTQwNTUzNTA1NTA0ODA4NTUwNQ==",
            "https://host.mintimate.cn/imageHost/getImage/MTQwNTUzNTUyMzc1MzE2ODg5OA==",
            "https://host.mintimate.cn/imageHost/getImage/MTQwNTUzNTg3NTcyMjM4MzM2MQ=="

    };

    @MemoryCache
    public static List<BannerItem> getBannerList() {
        List<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            BannerItem item = new BannerItem();
            item.imgUrl = urls[i];
            item.title = titles[i];

            list.add(item);
        }
        return list;
    }

    /**
     * 用于占位的空信息
     *
     * @return
     */
    @MemoryCache
    public static List<AudioFile> getEmptyNewAudios() {
        List<AudioFile> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new AudioFile("音频"));
        }
        return list;
    }

    /**
     * 用于占位的空信息
     *
     * @return
     */

    public static List<AudioFile> getDemoNewAudios() {
        AudioFileUtils audioFileUtils = new AudioFileUtils();
        List<AudioFile> audioList = new ArrayList<>();
        List<File> list = audioFileUtils.listFileSortByModifyTime(Environment.getExternalStorageDirectory().getPath() + "/" + "AduioBack" + "/audio");
        int ll = 0;
        for (File file : list) {
            audioList.add(new AudioFile(file.getPath(), "音频", DateTimeUtil.longToDate(file.lastModified()), file, file.getName()));
            ll++;
            Log.i("ZMS", ll + ":" + file.getName() + " = " + DateTimeUtil.formatDateTime(file.lastModified()));
        }
        Log.i("method", "查文件被执行");
        audioList.sort(new Comparator<AudioFile>() {
            @Override
            public int compare(AudioFile o1, AudioFile o2) {
                if (DateTimeUtil.compareDate(o1.getDate(), o2.getDate()))
                    return 1;
                else return -1;
            }
        });

        return audioList;
    }

}
