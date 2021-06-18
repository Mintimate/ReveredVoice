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

package com.kuang.templateproject.adapter.entity;


import com.kuang.templateproject.utils.Date.DateTimeUtil;

import java.io.File;
import java.util.Date;

public class AudioFile {
    private String filePath;
    private String filename;
    private String tag;
    private Date date;
    private File file;
    private String back_filePath;
    private File back_file;

    public AudioFile(String filePath, String tag, Date date, File file, String filename, String back_filePath, File back_file) {
        this.filePath = filePath;
        this.tag = tag;
        this.date = date;
        this.file = file;
        this.back_filePath = back_filePath;
        this.filename = filename;
        this.back_file = back_file;
    }
    public AudioFile(String filePath, String tag, Date date, File file, String filename) {
        this.filePath = filePath;
        this.tag = tag;
        this.date = date;
        this.filename = filename;
        this.file = file;

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public AudioFile(String tag) {
       this.tag =tag;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getBack_filePath() {
        return back_filePath;
    }

    public void setBack_filePath(String back_filePath) {
        this.back_filePath = back_filePath;
    }

    public File getBack_file() {
        return back_file;
    }

    public void setBack_file(File back_file) {
        this.back_file = back_file;
    }

    @Override
    public String toString() {
        return "AudioFile{" +
                "filePath='" + filePath + '\'' +
                ", filename='" + filename + '\'' +
                ", tag='" + tag + '\'' +
                ", date=" + DateTimeUtil.DateToStr(date) +
                ", file=" + file +
                ", back_filePath='" + back_filePath + '\'' +
                ", back_file=" + back_file +
                '}';
    }
}
