package com.vince.andwidget.scratch;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vince.andwidget.R;

/**
 * Created by tianweixin on 2016-10-28.
 */

public class ScratchTestActivity extends Activity {
    private ScratchView scratchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch);
        scratchView = (ScratchView) findViewById(R.id.scratch_view);
        FrameLayout scratchEntry = scratchView.getScratchEntryLayout();
        scratchEntry.setBackgroundColor(Color.BLUE);
        TextView tv = new TextView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER);
        tv.setText("欢迎使用ScratchView");
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(30);
        scratchEntry.addView(tv);
    }
}
