package com.bizbrolly.bluetoothlibrary;

import android.util.Log;

/**
 * Created by Akash on 17/04/17.
 */

public class Logger {
    private static String appTag;

    private Logger(){}

    public static void setTag(String tag){
        appTag = tag;
    }

    public static void log(String message){
        Log.e(appTag, message);
    }
}
