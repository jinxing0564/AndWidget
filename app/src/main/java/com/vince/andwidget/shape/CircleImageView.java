package com.vince.andwidget.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by tianweixin on 2016-11-24.
 */

public class CircleImageView extends ImageView {
    private int size;
    private BitmapDrawable drawable;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth()- getPaddingLeft() - getPaddingRight();
        int h = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        size = Math.min(w, h);
        if (this.getParent() instanceof ViewGroup) {
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (lp == null) {
                return;
            }
            lp.width = size + getPaddingLeft() + getPaddingRight();
            lp.height = size + getPaddingTop() + getPaddingBottom();
//            requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (size > 0 && drawable != null) {
            canvas.drawBitmap(createCircleImage(drawable, size), getPaddingLeft(), getPaddingTop(), null);
        }
    }


    private Bitmap createCircleImage(Bitmap source, int min) {
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Bitmap bitmap = Bitmap.createScaledBitmap(source, min, min, false);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return target;
    }

    private Bitmap createCircleImage(Drawable drawable, int min) {
        Bitmap bitmap = getBitmap(drawable, min);
        return createCircleImage(bitmap, min);
    }

    private Bitmap getBitmap(Drawable drawable, int min) {
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        drawable.setBounds(0, 0, size, size);
        drawable.draw(canvas);
        return target;
    }
}
