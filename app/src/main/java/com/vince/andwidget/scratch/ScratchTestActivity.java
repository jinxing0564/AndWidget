package com.vince.andwidget.scratch;

import android.app.Activity;
import android.os.Bundle;

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
        scratchView.setWaterMark(R.drawable.alipay);
    }
}
