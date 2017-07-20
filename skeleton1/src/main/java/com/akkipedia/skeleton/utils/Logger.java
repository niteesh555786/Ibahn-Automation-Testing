package com.akkipedia.skeleton.utils;

import android.util.Log;

/**
 * Created by Akash on 17/04/17.
 */

public class Logger {
    private static String appTag = "TESTING";

    private Logger(){}

    public static void setTag(String tag){
        appTag = tag;
    }

    public static void log(String message){
        Log.e(appTag, message);
    }
    public static void log(String tag, String message){
        Log.e(tag, message);
    }
}
