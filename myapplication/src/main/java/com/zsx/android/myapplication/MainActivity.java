package com.zsx.android.myapplication;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import mabeijianxi.camera.LocalMediaCompress;
import mabeijianxi.camera.MediaRecorderActivity;
import mabeijianxi.camera.model.AutoVBRMode;
import mabeijianxi.camera.model.LocalMediaConfig;
import mabeijianxi.camera.model.MediaRecorderConfig;
import mabeijianxi.camera.model.OnlyCompressOverBean;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 0);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                luxiang();
            }
        });
    }

    //录制视频
    private void luxiang() {
        // 配置录制参宿
        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .doH264Compress(new AutoVBRMode()
//                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
                )
                .setMediaBitrateConfig(new AutoVBRMode()
//                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
                )
                .smallVideoWidth(520)
                .smallVideoHeight(400)
                .maxFrameRate(25)
                .captureThumbnailsTime(1)//缩略图剪裁起始时间
                .recordTimeMax(10 * 1000)//最大录制时间
                .recordTimeMin((2 * 1000))//最短录制时间
                .build();
        //开始录制
        MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity.class.getName(), config);
    }

    //压缩指定视频
    private void yasuo(final String path) {
        //        final String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/相机/video_20171019_160839.mp4";
        File file = new File(path);
        Log.e("aaaaaaa", file.exists() + "");
        if (file.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //设置压缩参数
                    LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                    final LocalMediaConfig config = buidler
                            .setVideoPath(path)
                            .captureThumbnailsTime(1)
                            .doH264Compress(new AutoVBRMode())
                            .setFramerate(15)
                            .setScale(1.0f)
                            .build();
                    //开始压缩
                    OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
                    if (onlyCompressOverBean.isSucceed()) {
                        Log.e("aaaaaaa===ok", onlyCompressOverBean.getVideoPath());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyApplication.context, "成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyApplication.context, "失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }).start();

        }

    }
}
