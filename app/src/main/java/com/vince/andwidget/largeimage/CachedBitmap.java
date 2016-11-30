package com.vince.andwidget.largeimage;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by tianweixin on 2016-11-29.
 */

public class CachedBitmap {
    private Bitmap cachedBitmap;
    private Rect cachedRect;

    public CachedBitmap(Bitmap cachedBitmap, Rect cachedRect) {
        this.cachedBitmap = cachedBitmap;
        this.cachedRect = cachedRect;
    }

    public Bitmap getCachedBitmap() {
        return cachedBitmap;
    }

    public void setCachedBitmap(Bitmap cachedBitmap) {
        this.cachedBitmap = cachedBitmap;
    }

    public Rect getCachedRect() {
        return cachedRect;
    }

    public void setCachedRect(Rect cachedRect) {
        this.cachedRect = cachedRect;
    }
}
