package com.vince.andwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vince.andwidget.scratch.ScratchTestActivity;
import com.vince.andwidget.shape.ShapeImageTestActivity;
import com.vince.andwidget.video.VideoTestActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnVideo;
    private Button btnScratch;
    private Button btnShape;

    private void assignViews() {
        btnVideo = (Button) findViewById(R.id.btn_video);
        btnScratch = (Button) findViewById(R.id.btn_scratch);
        btnShape = (Button) findViewById(R.id.btn_shape);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        assignViews();
        btnVideo.setOnClickListener(this);
        btnScratch.setOnClickListener(this);
        btnShape.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_video:
                toActivity(VideoTestActivity.class);
                break;
            case R.id.btn_scratch:
                toActivity(ScratchTestActivity.class);
                break;
            case R.id.btn_shape:
                toActivity(ShapeImageTestActivity .class);
                break;
        }
    }

    private void toActivity(Class activity) {
        Intent it = new Intent(this, activity);
        startActivity(it);
    }

}
