package com.major.avd.util;

import android.os.Environment;

import java.io.File;

/**
 * 作者:meijie
 * 包名:com.major.avd.util
 * 工程名:AudioVideoDev
 * 时间:2018/9/21 17:09
 * 说明:
 */
public class Util {

    public static String getFilePath() {
        File adv = new File(Environment.getExternalStorageDirectory(), "avd");
        if (!adv.exists()) {
            adv.mkdirs();
        }
        return adv.getAbsolutePath();
    }
}
