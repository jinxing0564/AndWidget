package com.vince.andwidget.scratch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.vince.andwidget.R;

/**
 * Created by tianweixin on 2016-11-1.
 */

public class ScratchView extends FrameLayout {
    private FrameLayout flytEntry;
    private ScratchMaskView scratchMaskView;

    public ScratchView(Context context) {
        super(context);
        init();
    }

    public ScratchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchView);
        initAttrs(typedArray);
    }

    public ScratchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchView, defStyleAttr, 0);
        initAttrs(typedArray);
    }

    private void init() {
        this.removeAllViews();
        flytEntry = new FrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        flytEntry.setLayoutParams(layoutParams);
        this.addView(flytEntry);

        scratchMaskView = new ScratchMaskView(getContext());
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scratchMaskView.setLayoutParams(layoutParams2);
        this.addView(scratchMaskView);
    }

    private void initAttrs(TypedArray typedArray) {
        int color = typedArray.getColor(R.styleable.ScratchView_mask_color, getResources().getColor(R.color.color_mask));
        float width = typedArray.getDimension(R.styleable.ScratchView_water_mark_width, -1f);
        float height = typedArray.getDimension(R.styleable.ScratchView_water_mark_height, -1f);
        int percent = typedArray.getInt(R.styleable.ScratchView_finish_percent, 50);
        int markRes = typedArray.getResourceId(R.styleable.ScratchView_water_mark, -1);
        float scratchWidth = typedArray.getDimension(R.styleable.ScratchView_scratch_width, -1f);
        setMaskColor(color);
        if (markRes >= 0) {
            if (width > 0 && height > 0) {
                setWaterMark(markRes, (int) width, (int) height);
            } else {
                setWaterMark(markRes);
            }
        }
        setFinishPercent(percent);
        if (scratchWidth > 0) {
            setScratchWidth((int) scratchWidth);
        }
        typedArray.recycle();
    }

    public FrameLayout getScratchEntryLayout() {
        return flytEntry;
    }

    public void setScratchWidth(int width) {
        scratchMaskView.setScratchWidth(width);
    }

    public void setWaterMark(Bitmap bitmap) {
        scratchMaskView.setWaterMark(bitmap);
    }

    public void setWaterMark(Bitmap bitmap, int w, int h) {
        scratchMaskView.setWaterMark(bitmap, w, h);
    }

    public void setWaterMark(int markRes) {
        if (markRes != -1) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), markRes);
            setWaterMark(bitmap);
        }
    }

    public void setWaterMark(int markRes, int w, int h) {
        if (markRes != -1) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), markRes);
            setWaterMark(bitmap, w, h);
        }
    }

    public void setFinishPercent(int percent) {
        scratchMaskView.setFinishPercent(percent);
    }

    public void setMaskColor(int color) {
        scratchMaskView.setMaskColor(color);
    }
}
