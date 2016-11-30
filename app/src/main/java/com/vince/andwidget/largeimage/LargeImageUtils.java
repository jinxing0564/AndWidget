package com.vince.andwidget.largeimage;

import android.graphics.Rect;
import android.util.Log;

import static java.lang.Runtime.getRuntime;

/**
 * Created by tianweixin on 2016-11-30.
 */

public class LargeImageUtils {
    public static long getMaxMemory() {
        long mem = getRuntime().maxMemory();
        Log.d("jinxing", "max mem = " + mem);
        return mem;
    }

    public static long getFreeMemory() {
        long mem = getRuntime().freeMemory();
        Log.d("jinxing", "free mem = " + mem);
        return mem;
    }

    public static long getTotalMemory() {
        long mem = Runtime.getRuntime().totalMemory();
        Log.d("jinxing", "total mem = " + mem);
        return mem;
    }

    public static boolean memoryEnough() {
        return getTotalMemory() < getMaxMemory() * 7 / 8;
    }

    public static int computeLen(Rect rect1, Rect rect2) {
        int x1 = (rect1.left + rect1.right) / 2;
        int y1 = (rect1.top + rect1.bottom) / 2;
        int x2 = (rect2.left + rect2.right) / 2;
        int y2 = (rect2.top + rect2.bottom) / 2;
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }
}
