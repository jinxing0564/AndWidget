package com.vince.andwidget.largeimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.vince.andwidget.mechanism.AndThread;
import com.vince.andwidget.mechanism.AndThreadPool;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tianweixin on 2016-11-29.
 */

public class LargeImageView extends View {
    private int viewWidth;
    private int viewHeight;
    private InputStream inputStream;
    private int srcWidth;
    private int srcHeight;

    private Rect srcRect = new Rect(); //用于记录显示区域
    private Rect cachedRect = new Rect(); //用于当前缓存的区域

    private int startX;
    private int startY;
    private long startTime;
    private long deltTime = 100;

    private boolean firstShowing = true;

    CachedBitmap cachedBitmap;
    private CacheRegionThreadPool threadPool;

    public LargeImageView(Context context) {
        super(context);
        init();
    }

    public LargeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LargeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        threadPool = new CacheRegionThreadPool(5);
        firstShowing = true;
    }

    public void setSrc(InputStream is) {
        reset();
        inputStream = is;
        getSrcSize();
    }

    private void reset() {
        inputStream = null;
        srcWidth = 0;
        srcHeight = 0;
        srcRect.set(0, 0, 0, 0);
        firstShowing = true;
    }

    private void getSrcSize() {
        if (inputStream == null) {
            return;
        }
        BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
        tmpOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, tmpOptions);
        srcWidth = tmpOptions.outWidth;
        srcHeight = tmpOptions.outHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (inputStream == null || viewHeight <= 0 || viewWidth <= 0) {
            return;
        }
        if (firstShowing) {
            initRect();
            startCache();
            firstShowing = false;
        }
        drawBitmap(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                startTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                int curX = (int) event.getX();
                int curY = (int) event.getY();
                reCalculateRect(curX - startX, curY - startY);
                startX = curX;
                startY = curY;
                firstShowing = false;
                postInvalidate();
                long curTime = System.currentTimeMillis();
                if (curTime - startTime > deltTime) {
                    startCache();
                    startTime = curTime;
                }
                break;
            case MotionEvent.ACTION_UP:
                startCache();
                break;
        }

        return true;
    }

    private void startCache() {
        if (LargeImageUtils.memoryEnough()) {
            threadPool.addThread(new CacheRegionThread(cachedRect));
        }else{
            threadPool.cleanPool();
            System.gc();
        }
    }

    private void initRect() {
        if (inputStream == null) {
            return;
        }
        srcRect.left = 0;
        srcRect.top = 0;
        srcRect.right = Math.min(srcWidth, viewWidth);
        srcRect.bottom = Math.min(srcHeight, viewHeight);
        calculateCacheRect(srcRect);
    }

    private void calculateCacheRect(Rect srcRect) {
        cachedRect.left = srcRect.left - srcRect.width();
        cachedRect.top = srcRect.top - srcRect.height();
        cachedRect.right = srcRect.right + srcRect.width();
        cachedRect.bottom = srcRect.bottom + srcRect.height();

        cachedRect.left = Math.max(0, cachedRect.left);
        cachedRect.top = Math.max(0, cachedRect.top);
        cachedRect.right = Math.min(cachedRect.right, srcWidth);
        cachedRect.bottom = Math.min(cachedRect.bottom, srcHeight);
    }

    private void reCalculateRect(int deltX, int deltY) {
        doCalculateRect(srcRect, deltX, deltY);
        calculateCacheRect(srcRect);
    }

    private void doCalculateRect(Rect rect, int deltX, int deltY) {
        int moveX;
        if (deltX > 0) {
            //向右滑
            int left = rect.left;
            if (left - deltX < 0) {
                moveX = left;
            } else {
                moveX = deltX;
            }
        } else {
            int right = rect.right;
            if (right - deltX > srcWidth) {
                moveX = right - srcWidth;
            } else {
                moveX = deltX;
            }
        }
        rect.left = rect.left - moveX;
        rect.right = rect.right - moveX;

        int moveY;
        if (deltY > 0) {
            //向下滑
            int top = rect.top;
            if (top - deltY < 0) {
                moveY = top;
            } else {
                moveY = deltY;
            }
        } else {
            int bottom = rect.bottom;
            if (bottom - deltY > srcHeight) {
                moveY = bottom - srcHeight;
            } else {
                moveY = deltY;
            }
        }
        rect.top = rect.top - moveY;
        rect.bottom = rect.bottom - moveY;

    }

    //剪切图片太耗时，放弃使用
    //使用可能有线程同步问题
    private Bitmap getRegionBitmapFromCache() {
        Bitmap bitmap = null;
        if (cachedBitmap != null) {
            Rect cRect = cachedBitmap.getCachedRect();
            Bitmap cBitmap = cachedBitmap.getCachedBitmap();
            int left = srcRect.left - cRect.left;
            int top = srcRect.top - cRect.left;
            long time1 = System.currentTimeMillis();
            bitmap = Bitmap.createBitmap(cBitmap, left, top, srcRect.width(), srcRect.height(), null, false);
            long time2 = System.currentTimeMillis();
            Log.d("LargeImageView", "cut time = " + (time2 - time1));
        }
        return bitmap;
    }

    private synchronized void drawBitmap(Canvas canvas) {
        if (cachedBitmap != null) {
            Rect cRect = cachedBitmap.getCachedRect();
            int left = srcRect.left - cRect.left;
            int top = srcRect.top - cRect.top;
            canvas.drawBitmap(cachedBitmap.getCachedBitmap(), -left, -top, null);
        }
    }

    private synchronized void updateCachedBitmap(CachedBitmap cachedBitmap) {
        boolean useCache = true;
        if (this.cachedBitmap != null) {
            int lastLen = LargeImageUtils.computeLen(srcRect, this.cachedBitmap.getCachedRect());
            int nowLen = LargeImageUtils.computeLen(srcRect, cachedBitmap.getCachedRect());
            if (nowLen < lastLen) {
                useCache = true;
            } else {
                useCache = false;
            }
        }
        if (useCache) {
            this.cachedBitmap = cachedBitmap;
            postInvalidate();
        }
    }

    public class CacheRegionThread extends AndThread {
        private Rect threadRect;

        public CacheRegionThread(Rect cacheRect) {
            this.threadRect = new Rect(cacheRect);
        }

        public Rect getThreadRect() {
            return threadRect;
        }

        @Override
        public void deal() {
            if (threadRect == null || inputStream == null) {
                Log.d("LargeImageView", "cache bitmap error: cacheRect == null or inputStream == null");
                return;
            }
            if (!LargeImageUtils.memoryEnough()) {
                threadPool.cleanPool();
                System.gc();
                return;
            }
            Log.d("LargeImageView", "deal startRegion:" + System.currentTimeMillis());
            try {
                BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = bitmapRegionDecoder.decodeRegion(threadRect, options);
                updateCachedBitmap(new CachedBitmap(bitmap, threadRect));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("LargeImageView", "deal endRegion:" + System.currentTimeMillis());
        }

    }

    public class CacheRegionThreadPool extends AndThreadPool {

        public CacheRegionThreadPool(int size) {
            super(size);
        }

        @Override
        public synchronized int getFarMostPos(AndThread nowThread) {
            AndThread[] threads = getThreads();
            int farPos = 0;
            int far = 0;
            for (int i = 0; i < getPoolSize(); i++) {
                CacheRegionThread thread = (CacheRegionThread) threads[i];

                if (thread == null) {
                    return i;
                }
                int len = LargeImageUtils.computeLen(thread.getThreadRect(), ((CacheRegionThread) nowThread).getThreadRect());
                if (len >= far) {
                    farPos = i;
                    far = len;
                }
            }
            return farPos;
        }
    }

}
