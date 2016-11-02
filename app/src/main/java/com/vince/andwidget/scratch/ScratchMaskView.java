package com.vince.andwidget.scratch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.vince.andwidget.R;
import com.vince.andwidget.jni.AndJNI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tianweixin on 2016-10-28.
 */

public class ScratchMaskView extends View {
    private final int ON_SCRATCH_FINISH = 0x1;
    private int defaultPercent = 90;
    int maskColor = getResources().getColor(R.color.color_mask);
    private Bitmap maskBitmap;
    private Paint maskPaint;
    private Paint erasePaint;
    private Path erasePath;

    float nowX;
    float nowY;
    private Canvas maskCanvas;

    private int touchSlop;
    private BitmapDrawable markDrawable;
    private Bitmap markBitmap;

    private boolean scratchFinish = false;
    private int finishPercent = defaultPercent;

    ExecutorService cachedThreadPool;

    public ScratchMaskView(Context context) {
        super(context);
        init(context);
    }

    public ScratchMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScratchMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        maskPaint = new Paint();
        maskPaint.setStyle(Paint.Style.FILL);
        maskPaint.setColor(maskColor);
        maskPaint.setAntiAlias(true);
        maskPaint.setDither(true);

        erasePaint = new Paint();
        erasePaint.setAntiAlias(true);
        erasePaint.setAntiAlias(true);
        erasePaint.setStyle(Paint.Style.STROKE);
        erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        erasePaint.setStrokeCap(Paint.Cap.ROUND);
        erasePaint.setStrokeWidth(80);
        erasePath = new Path();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        touchSlop = viewConfiguration.getScaledTouchSlop();
        cachedThreadPool = Executors.newFixedThreadPool(5);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawBitmap(maskBitmap, 0, 0, maskPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createMask(w, h);
    }

    private void createMask(int w, int h) {
        scratchFinish = false;
        maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        maskCanvas = new Canvas(maskBitmap);
        maskCanvas.drawRect(0, 0, w, h, maskPaint);
        if (markBitmap != null) {
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(markBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            maskCanvas.drawRect(0, 0, w, h, paint);
        }
    }

    public void setWaterMark(Bitmap mark, int markWidth, int markHeight) {
        if (mark != null) {
            markBitmap = Bitmap.createScaledBitmap(mark, markWidth, markHeight, false);
        }
    }

    public void setWaterMark(Bitmap mark) {
        if (mark != null) {
            markBitmap = mark;
        }
    }

    public void setFinishPercent(int percent) {
        this.finishPercent = percent;
    }

    public void setMaskColor(int color) {
        maskColor = color;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (finished()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startErase(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                erase(event.getX(), event.getY());
                break;
            default:
                stopErase();
                break;
        }
        postInvalidate();
        return true;
    }

    private void startErase(float x, float y) {
        nowX = x;
        nowY = y;
        erasePath.reset();
        erasePath.moveTo(nowX, nowY);
    }

    private void erase(float x, float y) {
        float dx = Math.abs(x - nowX);
        float dy = Math.abs(y - nowY);
        if (dx >= touchSlop || dy >= touchSlop) {
            nowX = x;
            nowY = y;
            erasePath.lineTo(x, y);
            maskCanvas.drawPath(erasePath, erasePaint);
        }
    }

    private void stopErase() {
        nowX = 0;
        nowY = 0;
        erasePath.reset();
//        checkFinish(getWidth(), getHeight(), finishPercent);
        checkFinish();
    }

    private void checkFinish() {
        if (!cachedThreadPool.isShutdown()) {
            cachedThreadPool.execute(new CheckRunnable());
        }
    }

    private void checkFinish(int width, int height, int percent) {
        if (finished()) {
            return;
        }
        final int pixels[] = new int[width * height];
        new AsyncTask<Integer, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(Integer... params) {
                int width = params[0];
                int height = params[1];
                int percent = params[2];
                maskBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                int allPexels = width * height;
                int emptyPexels = 0;
                //使用JNI来提高循环效率
                AndJNI jni = new AndJNI();
                emptyPexels = jni.andloop(pixels);
                if (emptyPexels >= (percent / 100f) * allPexels) {
                    return true;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    clearAll();
                }
                super.onPostExecute(aBoolean);
            }
        }.execute(width, height, percent);
    }

    private void clearAll() {
        if (finished()) {
            return;
        }
        scratchFinish();
        int width = getWidth();
        int height = getHeight();

        maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        postInvalidate();
    }

    private boolean finished() {
        return scratchFinish;
    }

    private void scratchFinish() {
        scratchFinish = true;
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ON_SCRATCH_FINISH) {
                if (!cachedThreadPool.isShutdown()) {
                    cachedThreadPool.shutdownNow();
                }
                clearAll();
            }
            super.handleMessage(msg);
        }
    };

    class CheckRunnable implements Runnable {
        @Override
        public void run() {
            if (finished()) {
                return;
            }
            int width = getWidth();
            int height = getHeight();
            int pixels[] = new int[width * height];
            maskBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            int allPixels = width * height;
            int emptyPixels = 0;

            //使用JNI来提高循环效率
            AndJNI jni = new AndJNI();
            emptyPixels = jni.andloop(pixels);

            if (emptyPixels >= (finishPercent / 100f) * allPixels) {
                handler.sendEmptyMessage(ON_SCRATCH_FINISH);
            }
        }
    }

}
