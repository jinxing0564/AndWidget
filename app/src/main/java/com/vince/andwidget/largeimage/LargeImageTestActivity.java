package com.vince.andwidget.largeimage;

import android.app.Activity;
import android.os.Bundle;

import com.vince.andwidget.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tianweixin on 2016-11-29.
 */

public class LargeImageTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);
        LargeImageView ivLarge = (LargeImageView) findViewById(R.id.iv_large);

        try {
            InputStream inputStream = getAssets().open("Alongtheriver_QingMing.jpg");
            ivLarge.setSrc(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
