package com.vince.andwidget.jni;

/**
 * Created by tianweixin on 2016-11-2.
 */

public class AndJNI {
    static {
        System.loadLibrary("andjni");
    }

    public static native int andloop(int[] piexls);
}
