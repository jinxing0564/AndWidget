package com.vince.andwidget.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by tianweixin on 2016-11-28.
 * BitmapShader来实现
 */

public class RoundImageView extends ImageView {
    private int size;

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int h = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        size = Math.min(w, h);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        Drawable drawable = getDrawable();
        if (size > 0 && drawable != null) {
            Paint paint = getPaint(drawable);
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop());
            canvas.drawCircle(size / 2, size / 2, size / 2, paint);
            canvas.restore();
        }
    }

    private Paint getPaint(Drawable drawable) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap bitmap = getBitmap(drawable);

        int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        float scale = size * 1f / bSize;
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        shader.setLocalMatrix(matrix);

        paint.setShader(shader);
        return paint;
    }

    private Bitmap getBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        if (w < 0 || h < 0) {
            w = size;
            h = size;
        }
        Bitmap target = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);

        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return target;
    }
}
