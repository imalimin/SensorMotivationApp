package com.limingyilr.sensormotivation.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by lanqx on 2014/5/9.
 */
public class LogText {
    private String PATH;
    private String name = "";

    public LogText(String name) {
        this.name = name + ".txt";
        this.PATH = "/storage/sdcard0/";
        File file = new File(PATH);
        if(!file.exists()) {
            file.mkdir();
        }
    }

    public void addLog(String msg) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                FileWriter writer = new FileWriter(PATH + name, true);
                writer.write("[" + getTime() + "]" + " " + msg + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//                File targetFile = new File(PATH + name);
//                FileOutputStream fos = null;
//                fos = new FileOutputStream(targetFile);
//                fos.write(msg.getBytes());
//                fos.close();

        }
    }

    private void delFile() {
        File file = new File(PATH + name);
        if (file.exists()) {
            file.delete();
        }
    }

    private String getTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sDateFormat.format(new java.util.Date());
    }
}
