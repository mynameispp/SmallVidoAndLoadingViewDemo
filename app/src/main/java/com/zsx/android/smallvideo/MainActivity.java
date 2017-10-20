package com.zsx.android.smallvideo;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 0);
        initView();
    }


    private void luxiang(final String path){
        // 录制
        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .doH264Compress(new AutoVBRMode()
//                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
                )
                .setMediaBitrateConfig(new AutoVBRMode()
//                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
                )
                .smallVideoWidth(480)
                .smallVideoHeight(360)
                .recordTimeMax(6 * 1000)
                .maxFrameRate(20)
                .captureThumbnailsTime(1)
                .recordTimeMin((int) (1.5 * 1000))
                .build();
//        MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity1, config);
    }

    private void yasuo(final String path){
        //        final String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/相机/video_20171019_160839.mp4";
        File file=new File(path);
        Log.e("aaaaaaa",file.exists()+"");
        if (file.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                    final LocalMediaConfig config = buidler
                            .setVideoPath(path)
                            .captureThumbnailsTime(1)
                            .doH264Compress(new AutoVBRMode())
                            .setFramerate(15)
                            .setScale(1.0f)
                            .build();
                    OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
                    if (onlyCompressOverBean.isSucceed()){
                        Log.e("aaaaaaa===ok",onlyCompressOverBean.getVideoPath());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyApplication.context,"成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MyApplication.context,"失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }).start();

        }

    }

}
