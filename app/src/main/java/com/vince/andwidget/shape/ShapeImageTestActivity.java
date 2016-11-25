package com.vince.andwidget.shape;

import android.app.Activity;
import android.os.Bundle;

import com.vince.andwidget.R;

/**
 * Created by tianweixin on 2016-11-24.
 */

public class ShapeImageTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_image);
        CircleImageView imageVIew = (CircleImageView) findViewById(R.id.cimage);
//        imageVIew.setBackgroundColor(Color.RED);
//        imageVIew.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.mask_bg));
//        imageVIew.setImageResource(R.drawable.image_test);

//        imageVIew.setImageDrawable(getResources().getDrawable(R.drawable.mask_bg));

    }
}
