package com.vince.andwidget.video;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.vince.andwidget.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by tianweixin on 2016-10-25.
 */

public class VideoTestActivity extends Activity implements View.OnClickListener {

    private Button btnType1;
    private Button btnType2;
    private Button btnType3;
    private VideoView videoView;
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initViews();
    }

    private void initViews() {
        assignViews();
        btnType1.setOnClickListener(this);
        btnType2.setOnClickListener(this);
        btnType3.setOnClickListener(this);

        videoView.setVisibility(View.GONE);
        surfaceView.setVisibility(View.GONE);
        holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        holder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//
//            }
//        });


    }

    private void assignViews() {
        btnType1 = (Button) findViewById(R.id.btn_type1);
        btnType2 = (Button) findViewById(R.id.btn_type2);
        btnType3 = (Button) findViewById(R.id.btn_type3);
        videoView = (VideoView) findViewById(R.id.video_view);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_type1:
                playVideoBySystem();
                break;
            case R.id.btn_type2:
                playWithVideoView();
                break;
            case R.id.btn_type3:
                playWithMediaPlayer();
                break;
        }
    }

    private String getVideoPath() {
        String video = Environment.getExternalStorageDirectory().getPath() + "/cc.mp4";
        if (new File(video).exists()) {
            return video;
        }
        Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
        return null;
    }

    private Uri getVideoUri() {
        String video = getVideoPath();
        if (video != null) {
            Uri uri = Uri.parse(video);
            return uri;
        }
        Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
        return null;
    }

    private void playVideoBySystem() {
        Uri uri = getVideoUri();
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mp4");
            startActivity(intent);
        }
    }

    private void playWithVideoView() {
        videoView.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.GONE);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(getVideoUri());
        videoView.start();
        videoView.requestFocus();

    }

    private void playWithMediaPlayer() {
        surfaceView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        //然后指定需要播放文件的路径，初始化MediaPlayer

        initMediaPlayer();
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(getVideoPath());
            player.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initMediaPlayer() {
        if (player != null) {
            player.reset();
            if (player.isPlaying()) {
                player.release();
            }
        }

        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    if (player != null) {
                        if (player.isPlaying())
                            player.stop();
                        player.reset();
                        player.release();
                        player = null;
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int vWidth = player.getVideoWidth();
                int vHeight = player.getVideoHeight();

                if (vWidth > surfaceView.getMeasuredWidth() || vHeight > surfaceView.getMeasuredHeight()) {
                    float wRatio = (float) vWidth / (float) surfaceView.getMeasuredWidth();
                    float hRatio = (float) vHeight / (float) surfaceView.getMeasuredHeight();
                    float ratio = Math.max(wRatio, hRatio);

                    vWidth = (int) Math.ceil((float) vWidth / ratio);
                    vHeight = (int) Math.ceil((float) vHeight / ratio);

                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
                    if (lp == null) {
                        lp = new FrameLayout.LayoutParams(vWidth, vHeight);
                    } else {
                        lp.width = vWidth;
                        lp.height = vHeight;
                    }
                    surfaceView.setLayoutParams(lp);
                }
                player.setDisplay(holder);
                player.start();
                player.seekTo(0);
            }
        });
    }
}
