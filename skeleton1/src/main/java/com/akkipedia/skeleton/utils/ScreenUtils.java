package com.akkipedia.skeleton.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;

/**
 * Created by Akash on 10/11/16.
 */

public class ScreenUtils {

    public static int getNavigationBarHeight(Context context) {
        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean hasNavBar(Activity activity) {
        try {
            Point realSize = new Point();
            Point screenSize = new Point();
            boolean hasNavBar = false;
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            realSize.x = metrics.widthPixels;
            realSize.y = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
            if (realSize.y != screenSize.y) {
                int difference = realSize.y - screenSize.y;
                int navBarHeight = 0;
                Resources resources = activity.getResources();
                int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    navBarHeight = resources.getDimensionPixelSize(resourceId);
                }
                if (navBarHeight != 0) {
                    if (difference == navBarHeight) {
                        hasNavBar = true;
                    }
                }
            }
            return hasNavBar;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public static long getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static long getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    public static int dpToPx(int dp){
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public static int pxToDp(int px){
        return (int)(px / Resources.getSystem().getDisplayMetrics().densityDpi);
    }
}
