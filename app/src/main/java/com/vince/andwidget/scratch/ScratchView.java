package com.vince.andwidget.scratch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.vince.andwidget.R;

/**
 * Created by tianweixin on 2016-10-28.
 */

public class ScratchView extends View {
    private int defaultPercent = 90;
    int maskColor = R.color.color_mask;
    private Bitmap maskBitmap;
    private Paint maskPaint;
    private Context context;
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

    public ScratchView(Context context) {
        super(context);
        init(context);
    }

    public ScratchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScratchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        maskPaint = new Paint();
        maskPaint.setStyle(Paint.Style.FILL);
        maskPaint.setColor(context.getResources().getColor(maskColor));
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
        //画水印，方法一
        if (markBitmap != null) {
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(markBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
            maskCanvas.drawRect(0, 0, w, h, paint);
        }
        //画水印，方法二
//        if (markDrawable != null) {
//            markDrawable.setBounds(0, 0, w, h);
//            markDrawable.draw(maskCanvas);
//        }
    }

    public void setWaterMark(int markRes, int markWidth, int markHeight) {
        if (markRes != -1) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), markRes);
            markBitmap = Bitmap.createScaledBitmap(bitmap, markWidth, markHeight, true);
        }
    }

    public void setWaterMark(int markRes) {
        if (markRes != -1) {
            markBitmap = BitmapFactory.decodeResource(getResources(), markRes);
            //水印，方法二
//            markDrawable = new BitmapDrawable(getResources(), markBitmap);
//            markDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
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
        checkFinish(getWidth(), getHeight(), finishPercent);

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
                for (int i = 0; i < allPexels; i++) {
                    if (pixels[i] == 0) {
                        emptyPexels++;
                    }
                }
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

    private void reset() {
        createMask(getWidth(), getHeight());
        postInvalidate();
    }

}
